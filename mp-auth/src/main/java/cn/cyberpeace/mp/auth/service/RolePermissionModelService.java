package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.target.TargetRoleBo;
import cn.cyberpeace.mp.auth.model.RolePermissionModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public interface RolePermissionModelService extends BaseModelService<RolePermissionModel, QueryParams> {

    /**
     * listTargetsByRoleIds
     *
     * @param roleIds
     * @return
     */
    List<TargetModel> listTargetsByRoleIds(List<String> roleIds);

    /**
     * listTargetRoleDtosByRoleIds
     *
     * @param roleIds
     * @return
     */
    List<TargetRoleBo> listTargetRoleDtosByRoleIds(List<String> roleIds);

    /**
     * bindRoleTargets
     *
     * @param roleId
     * @param targetIds
     * @param cleanOldTargets
     * @return boolean
     */
    boolean bindRoleTargets(String roleId, List<String> targetIds, boolean cleanOldTargets);

    /**
     * deleteByRoleId
     *
     * @param roleId
     * @return boolean
     */
    boolean deleteByRoleId(String roleId);

    /**
     * deleteByTargetId
     *
     * @param targetId
     * @return boolean
     */
    boolean deleteByTargetId(String targetId);

}
