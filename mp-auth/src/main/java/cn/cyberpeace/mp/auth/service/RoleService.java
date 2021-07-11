package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.role.RoleBo;
import cn.cyberpeace.mp.auth.bo.role.RolePermBo;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.request.role.RolePageRequest;
import cn.cyberpeace.mp.auth.request.role.RoleRequest;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;

import java.util.List;


public interface RoleService extends BaseModelService<RoleModel, QueryParams> {

    List<RoleModel> listByTenantId(String tenantId);

    List<RoleModel> listAll(RolePageRequest rolePageRequest);

    List<RoleModel> listByOrgId(String orgId);

    RoleModel getByIdent(String identifier);

    RoleModel getByName(String name);

    List<RoleModel> listByNames(List<String> names);

    boolean exists(String id);

    ListObjServiceResult getPageWithUserCnt(RolePageRequest rolePageRequest);

    RoleBo getRoleDto(String id);

    RolePermBo getRolePermDto(String id);

    RoleModel add(RoleRequest createRequest);

    RoleModel modify(RoleRequest modifyRequest);

    List<String> getSystemUnVisibleRoleIds();
}
