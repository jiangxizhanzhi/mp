package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.base.service.BaseModelService;

import java.util.List;

public interface UserRoleService extends BaseModelService<UserRoleModel> {

    List<RoleModel> listByUserId(String userId);
}
