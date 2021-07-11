package cn.cyberpeace.mp.frontadmin.service.sys;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeUserModel;

import java.util.List;


public interface SysNoticeUserService extends BaseModelService<SysNoticeUserModel, QueryParams> {

    int deleteByNoticeIds(List<String> noticeIds);

    int deleteByUser(String userId);

}
