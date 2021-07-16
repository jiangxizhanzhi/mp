package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.dto.user.UserOnlineReportPageRequest;
import cn.cyberpeace.mp.auth.dto.user.UserOnlineReportTask;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.model.UserOnlineReportModel;
import cn.cyberpeace.mp.auth.repository.UserOnlineReportMapper;
import cn.cyberpeace.mp.auth.service.UserOnlineReportService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.fields.UserOnlineReportFields;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.hutool.core.lang.Assert;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserOnlineReportServiceImpl extends BaseModelServiceImpl<UserOnlineReportModel, UserOnlineReportMapper, UserOnlineReportFields, QueryParams> implements UserOnlineReportService {

    // 每10分钟统计一次
    public static final long REFRESH_RATE = 60000 * 10;

    @Autowired
    public UserOnlineReportServiceImpl(UserOnlineReportMapper modelMapper, UserOnlineReportFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Autowired
    private UserService userService;


    @Scheduled(initialDelay = REFRESH_RATE, fixedRate = REFRESH_RATE)
    public void initSchedule() {
        log.info("schedule user online report task...");
//        UserOnlineReportTask task = new UserOnlineReportTask();
//        scheduler.schedule(task, 0, TimeUnit.MILLISECONDS);
    }


    public PageInfo page(UserOnlineReportPageRequest req) {
        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<UserOnlineReportModel> onlineReportModels = getModelMapper().listAll();
        PageInfo pageInfo = new PageInfo(onlineReportModels);

        return pageInfo;
    }

    @Override
    public boolean createOrUpdate(String userId, boolean isOnline) {
        Assert.notNull(userId, "userId不能为空！");

        ObjServiceResult<UserModel> result = userService.get(userId);
        UserModel userModel = result.getSource() == null ? null : result.getSource().getObj();
        if (Objects.isNull(userModel)) {
            throw new MpRuntimeException("用户id:" + userId + "不存在！");
        }
        UserDto userDto = userService.userModel2UserDto(userModel);

        // 更新
        UserOnlineReportModel onlineReportNew = new UserOnlineReportModel();
        onlineReportNew.setUser(userId);
        onlineReportNew.setUsername(userModel.getUsername());
        onlineReportNew.setName(userModel.getName());
        onlineReportNew.setOrg(userDto.getOrgs().stream().map(OrgModel::getPath).collect(Collectors.joining(",")));

        if (isOnline) {
            onlineReportNew.setIsOnline(1);
        } else {
            onlineReportNew.setIsOnline(0);
        }

        UserOnlineReportModel onlineReportOld = getModelMapper().getByUserId(userId);
        if (Objects.nonNull(onlineReportOld)) {
            // 更新
            // todo 更新在线时常 每6分钟检测一次？
            onlineReportNew.setTotalOnlineHours(onlineReportOld.getTotalOnlineHours() + 0.1);
            onlineReportNew.setId(onlineReportOld.getId());
            getModelMapper().update(onlineReportNew);
        } else {
            // 新增
            onlineReportNew.setTotalOnlineHours(0.1);
            getModelMapper().create(onlineReportNew);
        }

        return true;
    }
}
