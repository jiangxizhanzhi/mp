package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.RolePermissionModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.base.service.BaseModelService;

import java.util.List;

public interface RolePermissionService extends BaseModelService<RolePermissionModel> {

    List<TargetModel> listTargetsByRoleId(String roleId);


    boolean bindRoleTargets(String roleId, List<String> targetIds);

    // 清除角色绑定的所有权限
    boolean cleanRoleTargets(String roleId);

}
