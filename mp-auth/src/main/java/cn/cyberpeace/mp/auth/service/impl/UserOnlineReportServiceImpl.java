package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthLogConstant;
import cn.cyberpeace.mp.auth.dto.user.UserLogPageRequest;
import cn.cyberpeace.mp.auth.dto.user.UserOlReportCntDto;
import cn.cyberpeace.mp.auth.dto.user.UserOnlineCntDto;
import cn.cyberpeace.mp.auth.dto.user.UserOnlineReportPageRequest;
import cn.cyberpeace.mp.auth.dto.user.UserOnlineReportTask;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.UserOnlineReportModel;
import cn.cyberpeace.mp.auth.repository.UserOnlineReportMapper;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.service.UserHeartbeatService;
import cn.cyberpeace.mp.auth.service.UserLogService;
import cn.cyberpeace.mp.auth.service.UserOnlineReportService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.fields.UserOnlineReportFields;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.devops.log.model.OperationLogModel;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserOnlineReportServiceImpl extends BaseModelServiceImpl<UserOnlineReportModel, UserOnlineReportMapper, UserOnlineReportFields, QueryParams> implements UserOnlineReportService {

    // 每15分钟统计一次
    public static final long REFRESH_RATE_MINUTES = 15;

    public static final long REFRESH_RATE_MILLISECONDS = 60000 * REFRESH_RATE_MINUTES;

    // 每次间隔的次数(每分钟一次，15分钟就是15次，80% 就是12次）
    public static final double HEARTBEAT_PER_RATE = 0.8 * 15;

    @Autowired
    public UserOnlineReportServiceImpl(UserOnlineReportMapper modelMapper, UserOnlineReportFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    private static ExecutorService threadPool = null;

    @Autowired
    private UserHeartbeatService heartbeatService;

    @Autowired
    private UserOnlineReportService userOnlineReportService;

    @Autowired
    private OrgUserService orgUserService;

    @Autowired
    private UserLogService userLogService;

    @Autowired
    private UserService userService;


    @Scheduled(initialDelay = REFRESH_RATE_MILLISECONDS, fixedRate = REFRESH_RATE_MILLISECONDS)
    public void scheduleUserOlReport() {
        if (threadPool == null) {
            threadPool = getSingletonThreadPool();
        }

        log.info("schedule user online report task...");
        List<UserOlReportCntDto> list = heartbeatService.groupCntByUserId(LocalDateTime.now().minusMinutes(REFRESH_RATE_MINUTES));
        log.info("{}", list);
        for (UserOlReportCntDto userOlReportCntDto : list) {
            UserOnlineReportTask task = new UserOnlineReportTask(userOlReportCntDto, userOnlineReportService);
            threadPool.submit(task);
        }
    }


    public PageInfo page(UserOnlineReportPageRequest req) {
        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<UserOnlineReportModel> onlineReportModels = getModelMapper().listAll();
        PageInfo<UserOnlineReportModel> pageInfo = new PageInfo(onlineReportModels);

        List<UserOnlineReportModel> reports = pageInfo.getList();
        reports.forEach(r -> {
            // 只获取最新的一条日志
            UserLogPageRequest logPageRequest = new UserLogPageRequest();
            logPageRequest.setUsername(r.getUsername());
            logPageRequest.setPage(1);
            logPageRequest.setPageSize(1);
            PageInfo<OperationLogModel> logPage = userLogService.getUserLog(logPageRequest);
            List<OperationLogModel> logs = logPage.getList();
            if (CollectionUtil.isNotEmpty(logs) && Objects.nonNull(logs.get(0))) {
                // 动作
                r.setAction(logs.get(0).getOpType());
                // 对象
                r.setSubject(logs.get(0).getOpTarget());
            }
        });

        return pageInfo;
    }

    @Override
    public UserOnlineCntDto onlineUserCnt() {
        int olcnt = getModelMapper().total(new HumanCondition().filterEqual("is_online", UserOnlineReportModel.ONLINE));
        int totalUser = userService.totalUserCnt();

        UserOnlineCntDto userOnlineCntDto = new UserOnlineCntDto();
        userOnlineCntDto.setOnlineUser(olcnt);
        userOnlineCntDto.setTotalUser(totalUser);

        return userOnlineCntDto;
    }


    @Override
    public boolean createOrUpdate(UserOlReportCntDto userOlReportCntDto) {
        Assert.notNull(userOlReportCntDto, "userOlReportCntDto不能为空！");

        UserOnlineReportModel onlineReportNew = new UserOnlineReportModel();

        // 只选出组织ID最大的一个
//        String allorgPath = orgUserService.getAllOrgPathByUserId(userOlReportCntDto.getUid());
        List<OrgModel> orgs = orgUserService.listOrgsByUserId(userOlReportCntDto.getUid());
        Optional<OrgModel> optional = orgs.stream().max(Comparator.comparing(OrgModel::getPk));
        optional.ifPresent(o -> onlineReportNew.setOrg(o.getPath()));

        // 更新
        onlineReportNew.setUser(userOlReportCntDto.getUid());
        onlineReportNew.setUsername(userOlReportCntDto.getUsername());
        onlineReportNew.setName(userOlReportCntDto.getUname());

        // 查看15分钟内，收到的心跳次数，如果大于阈值，表示这15分钟内用户处于在线状态
        boolean isOnline = userOlReportCntDto.getCnt() > HEARTBEAT_PER_RATE;

        if (isOnline) {
            onlineReportNew.setIsOnline(UserOnlineReportModel.ONLINE);
        } else {
            onlineReportNew.setIsOnline(UserOnlineReportModel.OFFLINE);
        }

        UserOnlineReportModel onlineReportOld = getModelMapper().getByUserId(userOlReportCntDto.getUid());
        if (Objects.nonNull(onlineReportOld)) {
            // 更新在线时常 每15分钟统计一次，在线时长增加0.25小时
            if (isOnline) {
                onlineReportNew.setTotalOnlineHours(onlineReportOld.getTotalOnlineHours() + 0.25);
            }
            onlineReportNew.setId(onlineReportOld.getId());
            onlineReportNew.setUpdateTime(LocalDateTime.now());
            getModelMapper().update(onlineReportNew);
        } else {
            // 新增 在线时常 每15分钟统计一次，在线时长增加0.25小时
            if (isOnline) {
                onlineReportNew.setTotalOnlineHours(0.25);
            } else {
                onlineReportNew.setTotalOnlineHours(0.0);
            }
            onlineReportNew.setCreateTime(LocalDateTime.now());
            getModelMapper().create(onlineReportNew);
        }

        return true;
    }


    /**
     * 静态内部类的单例模式
     * @return
     */
    public static ExecutorService getSingletonThreadPool() {
        return ThreadPoolHolder.threadPoolExecutor;
    }

    private static class ThreadPoolHolder {
        static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("online-report-pool-%d").build();
        private static ExecutorService threadPoolExecutor = new ThreadPoolExecutor(20, 100, 20, TimeUnit.SECONDS,
                new ArrayBlockingQueue(100), namedThreadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());
    }


    /*private String convertOpType2Desc(String opType) {
        if(Objects.equals(opType, AuthLogConstant.TYPE_LOGIN)) {
            return "登录";
        }
        if(Objects.equals(opType, AuthLogConstant.TYPE_LOGOUT)) {
            return "登出";
        }
        if(Objects.equals(opType, AuthLogConstant.TYPE_ADD)) {
            return "添加";
        }
        if(Objects.equals(opType, AuthLogConstant.TYPE_REGISTER)) {
            return "注册";
        }
        if(Objects.equals(opType, AuthLogConstant.TYPE_UPDATE)) {
            return "更新";
        }
        if(Objects.equals(opType, AuthLogConstant.TYPE_DELETE)) {
            return "删除";
        }
        if(Objects.equals(opType, AuthLogConstant.TYPE_BATCH_ADD)) {
            return "批量添加";
        }
        if(Objects.equals(opType, AuthLogConstant.TYPE_BATCH_UPDATE)) {
            return "批量更新";
        }
        if(Objects.equals(opType, AuthLogConstant.TYPE_BATCH_DELETE)) {
            return "批量删除";
        }

        log.info("opType: {}", opType);
        return opType;
    }*/


}
