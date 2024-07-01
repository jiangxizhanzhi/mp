package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.role.RoleBo;
import cn.cyberpeace.mp.auth.bo.role.RolePermBo;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.request.role.RolePageRequest;
import cn.cyberpeace.mp.auth.request.role.RoleRequest;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;

import java.util.Collection;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public interface RoleService extends BaseModelService<RoleModel, QueryParams> {

    /**
     * listByTenantId
     *
     * @param tenantId
     * @return
     */
    List<RoleModel> listByTenantId(String tenantId);

    /**
     * listByIds
     *
     * @param ids
     * @return
     */
    List<RoleModel> listByIds(Collection<String> ids);

    /**
     * listAll
     *
     * @param rolePageRequest
     * @return
     */
    List<RoleModel> listAll(RolePageRequest rolePageRequest);

    /**
     * getByName
     *
     * @param name
     * @return
     */
    RoleModel getByName(String name);

    /**
     * listByNames
     *
     * @param names
     * @return
     */
    List<RoleModel> listByNames(List<String> names);

    /**
     * exists
     *
     * @param id
     * @return boolean
     */
    boolean exists(String id);

    /**
     * getPageWithUserCnt
     *
     * @param rolePageRequest
     * @return
     */
    ListObjServiceResult getPageWithUserCnt(RolePageRequest rolePageRequest);

    /**
     * getRoleDto
     *
     * @param id
     * @return
     */
    RoleBo getRoleDto(String id);

    /**
     * getRolePermDto
     *
     * @param id
     * @return
     */
    RolePermBo getRolePermDto(String id);

    /**
     * add
     *
     * @param createRequest
     * @return
     */
    RoleModel add(RoleRequest createRequest);

    /**
     * modify
     *
     * @param modifyRequest
     * @return
     */
    RoleModel modify(RoleRequest modifyRequest);

    /**
     * getSystemUnVisibleRoleIds
     *
     * @return
     */
    List<String> getSystemUnVisibleRoleIds();
}
