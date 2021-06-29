package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.util.List;

public interface RoleService extends BaseModelService<RoleModel, QueryParams> {

    List<RoleModel> listByTenantId(String tenantId);


    List<RoleModel> listByOrgId(String orgId);

    RoleModel getByIdent(String identifier);
}
