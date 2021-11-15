package cn.cyberpeace.mp.auth.service;

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

    List<TargetModel> listTargetsByRoleId(String roleId);


    boolean bindRoleTargets(String roleId, List<String> targetIds, boolean cleanOldTargets);

    boolean deleteByRoleId(String roleId);

    boolean deleteByTargetId(String targetId);

}
