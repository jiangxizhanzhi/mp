package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.UserHeartbeatModel;
import cn.cyberpeace.mp.auth.model.UserOnlineReportModel;
import cn.cyberpeace.mp.auth.repository.UserHeartbeatMapper;
import cn.cyberpeace.mp.auth.repository.UserOnlineReportMapper;
import cn.cyberpeace.mp.auth.service.UserHeartbeatService;
import cn.cyberpeace.mp.auth.service.UserOnlineReportService;
import cn.cyberpeace.mp.auth.service.fields.UserHeartbeatFields;
import cn.cyberpeace.mp.auth.service.fields.UserOnlineReportFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserHeartbeatServiceImpl extends BaseModelServiceImpl<UserHeartbeatModel, UserHeartbeatMapper, UserHeartbeatFields, QueryParams> implements UserHeartbeatService {

    @Autowired
    public UserHeartbeatServiceImpl(UserHeartbeatMapper modelMapper, UserHeartbeatFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

}
