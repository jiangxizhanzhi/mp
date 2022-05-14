package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.role.RoleDto;
import cn.cyberpeace.mp.auth.dto.role.RolePageRequest;
import cn.cyberpeace.mp.auth.dto.role.RolePermDto;
import cn.cyberpeace.mp.auth.dto.role.RoleRequest;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface RoleService extends BaseModelService<RoleModel, QueryParams> {

    List<RoleModel> listByTenantId(String tenantId);

    List<RoleModel> listAll();

    List<RoleModel> listAllExcludeSuperUserRole();

    List<RoleModel> listByOrgId(String orgId);

    RoleModel getByIdent(String identifier);

    RoleModel getByName(String name);

    List<RoleModel> listByNames(List<String> names);

    boolean exists(String id);

    PageInfo getPageWithUserCnt(RolePageRequest rolePageRequest);

    RoleDto getRoleDto(String id);

    RolePermDto getRolePermDto(String id);

    RoleModel add(RoleRequest createRequest);

    RoleModel modify(RoleRequest modifyRequest);
}
