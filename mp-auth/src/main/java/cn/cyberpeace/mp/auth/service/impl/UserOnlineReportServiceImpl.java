package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.dto.user.UserOnlineReportPageRequest;
import cn.cyberpeace.mp.auth.dto.user.UserOnlineReportTask;
import cn.cyberpeace.mp.auth.model.UserOnlineReportModel;
import cn.cyberpeace.mp.auth.repository.UserOnlineReportMapper;
import cn.cyberpeace.mp.auth.service.UserOnlineReportService;
import cn.cyberpeace.mp.auth.service.fields.UserOnlineReportFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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


    @Scheduled(initialDelay = REFRESH_RATE, fixedRate = REFRESH_RATE)
    public void initSchedule() {
        log.info("schedule user online report task...");
//        List<TabUsersChannelConfig> tabUsersChannelConfigs = this.tabUsersChannelConfigDao.query();
//        Map<String,String> remoteMap = tabUsersChannelConfigs.stream().collect(
//                Collectors.toMap(TabUsersChannelConfig::getChannelName,TabUsersChannelConfig::getChannelPublickey));
//        UserOnlineReportTask task = new UserOnlineReportTask();
//        scheduler.schedule(task, 0, TimeUnit.MILLISECONDS);
    }


    public PageInfo page(UserOnlineReportPageRequest req) {
        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<UserOnlineReportModel> onlineReportModels = getModelMapper().listAll();
        PageInfo pageInfo = new PageInfo(onlineReportModels);

        return pageInfo;
    }



}
