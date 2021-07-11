package cn.cyberpeace.mp.frontadmin.service;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeRoleModel;

import java.util.List;


public interface SysNoticeRoleService extends BaseModelService<SysNoticeRoleModel, QueryParams> {

    List<RoleModel> listByNoticeIds(List<String> noticeIds);

    int deleteByNoticeIds(List<String> noticeIds);

    int deleteByRole(String roleId);
}
