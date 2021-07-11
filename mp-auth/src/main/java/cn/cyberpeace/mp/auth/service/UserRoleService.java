package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.role.UserRoleStatisticBo;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.auth.request.role.UserRolePageRequest;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;

import java.util.List;


public interface UserRoleService extends BaseModelService<UserRoleModel, QueryParams> {

    List<RoleModel> listByUserId(String userId);

    List<RoleModel> listByUserIds(List<String> userIds);

    List<UserModel> listUsersByRoleIdNoRestrict(String roleId);

    ListObjServiceResult pageByUserId(UserRolePageRequest pageRequest);

    ListObjServiceResult pageByRoleId(UserRolePageRequest pageRequest);

    
    boolean bindUserRoles(String userId, List<String> roleIds, boolean cleanOldRoles);

    
    boolean bindRoleUsers(String roleId, List<String> userIds);

    
    boolean deleteByRoleIdAndUserIds(String roleId, List<String> userIds);

    boolean deleteByUserId(String userId);

    boolean deleteByRoleId(String roleId);

    
    List<UserRoleStatisticBo> getUserRoleStatistics();

}
