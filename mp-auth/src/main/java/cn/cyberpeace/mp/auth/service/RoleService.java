package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.role.RolePageRequest;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface RoleService extends BaseModelService<RoleModel, QueryParams> {

    List<RoleModel> listByTenantId(String tenantId);

    List<RoleModel> listAll();

    List<RoleModel> listByOrgId(String orgId);

    RoleModel getByIdent(String identifier);

    boolean exists(String id);

    PageInfo getPage(RolePageRequest rolePageRequest);
}
