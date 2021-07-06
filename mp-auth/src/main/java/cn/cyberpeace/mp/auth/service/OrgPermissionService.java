package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.OrgPermissionModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.lang.annotation.Target;
import java.util.List;
import java.util.Set;

public interface OrgPermissionService extends BaseModelService<OrgPermissionModel, QueryParams> {

    Set<TargetModel> listTargetsByOrgId(String orgId);

    Set<TargetModel> listTargetsByOrgIdIncludeAllParentsOrg(String orgId);

    boolean bindOrgTargets(String orgId, List<String> targetIds);

    boolean cleanOrgTargets(String orgId);


}
