package cn.cyberpeace.mp.frontadmin.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeReadModel;


public interface SysNoticeReadService extends BaseModelService<SysNoticeReadModel, QueryParams> {

    SysNoticeReadModel selectByUserIdAndNoticeId(String user, String noticeId);

    int increaseReadCount(String id);

    int countIsReadedOrIsUnPush(String user);
}
