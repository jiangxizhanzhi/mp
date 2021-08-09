package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.notice.SysNoticeRequest;
import cn.cyberpeace.mp.auth.model.SysNoticeModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

public interface SysNoticeService extends BaseModelService<SysNoticeModel, QueryParams> {


    SysNoticeModel create(SysNoticeRequest sysNoticeRequest);


}
