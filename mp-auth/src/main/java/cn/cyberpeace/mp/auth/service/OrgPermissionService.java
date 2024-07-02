package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.OrgPermissionModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.util.List;
import java.util.Set;

/**
 * @ClassName OrgPermissionService
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see BaseModelService
 */
public interface OrgPermissionService extends BaseModelService<OrgPermissionModel, QueryParams> {

    /**
     * listTargetsByOrgId
     *
     * @param orgId
     * @return
     */
    Set<TargetModel> listTargetsByOrgId(String orgId);

    /**
     * listTargetsByOrgIdIncludeAllParentsOrg
     *
     * @param orgId
     * @return
     */
    Set<TargetModel> listTargetsByOrgIdIncludeAllParentsOrg(String orgId);

    /**
     * bindOrgTargets
     *
     * @param orgId
     * @param targetIds
     * @param cleanOldTargets
     * @return boolean
     */
    boolean bindOrgTargets(String orgId, List<String> targetIds, boolean cleanOldTargets);

    /**
     * deleteByOrgId
     *
     * @param orgId
     * @return boolean
     */
    boolean deleteByOrgId(String orgId);

    /**
     * deleteByTargetId
     *
     * @param targetId
     * @return boolean
     */
    boolean deleteByTargetId(String targetId);


}
