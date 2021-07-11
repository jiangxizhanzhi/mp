package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.target.TargetRoleBo;
import cn.cyberpeace.mp.auth.model.RolePermissionModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.util.List;


public interface RolePermissionModelService extends BaseModelService<RolePermissionModel, QueryParams> {

    List<TargetModel> listTargetsByRoleIds(List<String> roleIds);

    List<TargetRoleBo> listTargetRoleDtosByRoleIds(List<String> roleIds);

    boolean bindRoleTargets(String roleId, List<String> targetIds, boolean cleanOldTargets);

    boolean deleteByRoleId(String roleId);

    boolean deleteByTargetId(String targetId);

}
