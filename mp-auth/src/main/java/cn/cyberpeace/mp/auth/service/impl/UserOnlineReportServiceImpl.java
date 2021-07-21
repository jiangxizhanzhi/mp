package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.dto.user.UserOlReportCntDto;
import cn.cyberpeace.mp.auth.dto.user.UserOnlineReportPageRequest;
import cn.cyberpeace.mp.auth.dto.user.UserOnlineReportTask;
import cn.cyberpeace.mp.auth.model.UserOnlineReportModel;
import cn.cyberpeace.mp.auth.repository.UserOnlineReportMapper;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.service.UserHeartbeatService;
import cn.cyberpeace.mp.auth.service.UserOnlineReportService;
import cn.cyberpeace.mp.auth.service.fields.UserOnlineReportFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.hutool.core.lang.Assert;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
        PageInfo pageInfo = new PageInfo(onlineReportModels);

        return pageInfo;
    }

    @Override
    public boolean createOrUpdate(UserOlReportCntDto userOlReportCntDto) {
        Assert.notNull(userOlReportCntDto, "userOlReportCntDto不能为空！");

        String allorgPath = orgUserService.getAllOrgPathByUserId(userOlReportCntDto.getUid());

        // 更新
        UserOnlineReportModel onlineReportNew = new UserOnlineReportModel();
        onlineReportNew.setUser(userOlReportCntDto.getUid());
        onlineReportNew.setUsername(userOlReportCntDto.getUsername());
        onlineReportNew.setName(userOlReportCntDto.getUname());
        onlineReportNew.setOrg(allorgPath);

        // 查看15分钟内，收到的心跳次数，如果大于阈值，表示这15分钟内用户处于在线状态
        boolean isOnline = userOlReportCntDto.getCnt() > HEARTBEAT_PER_RATE;

        if (isOnline) {
            onlineReportNew.setIsOnline(1);
        } else {
            onlineReportNew.setIsOnline(0);
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
}
