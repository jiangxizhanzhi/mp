package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.user.UserOlReportCntDto;
import cn.cyberpeace.mp.auth.model.SysConfigModel;
import cn.cyberpeace.mp.auth.model.UserHeartbeatModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface UserHeartbeatService extends BaseModelService<UserHeartbeatModel, QueryParams> {

    List<UserOlReportCntDto> groupCntByUserId(LocalDateTime time);

}
