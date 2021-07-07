package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.util.List;

public interface UserRoleService extends BaseModelService<UserRoleModel, QueryParams> {

    List<RoleModel> listByUserId(String userId);

    List<UserDto> listByRoleId(String roleId);

    // 绑定一个用户，多个角色 会清除用户原来绑定的角色
    boolean bindUserRoles(String userId, List<String> roleIds, boolean cleanOldRoles);

    // 绑定一个角色，多个用户
    boolean bindRoleUsers(String roleId, List<String> userIds);

    // 清除用户所有角色
    boolean deleteByUserId(String userId);

    // 清除用户所有角色
    boolean deleteByRoleId(String roleId);

    // 解绑一个角色，多个用户
    boolean deleteByRoleIdAndUserIds(String roleId, List<String> userIds);
}
