package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.util.List;

public interface UserRoleService extends BaseModelService<UserRoleModel, QueryParams> {

    List<RoleModel> listByUserId(String userId);

    boolean bindUserRoles(String userId, List<String> roleIds);

    // 清除用户所有角色
    boolean cleanUserRoles(String userId);
}
