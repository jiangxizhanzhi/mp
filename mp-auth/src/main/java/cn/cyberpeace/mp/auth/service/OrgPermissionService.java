package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.OrgPermissionModel;
import cn.cyberpeace.mp.base.service.BaseModelService;

import java.util.List;

public interface OrgPermissionService extends BaseModelService<OrgPermissionModel> {

    boolean bindOrgTargets(String orgId, List<String> targetIds);

    boolean cleanOrgTargets(String orgId);


}
