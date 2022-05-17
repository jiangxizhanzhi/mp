package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.dto.role.RoleDto;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Mapper
@Repository
@ModelClass(RoleModel.class)
public interface RoleMapper extends BaseModelMapper<RoleModel> {

    List<RoleModel> listByTenantId(@Param("tenantId") String tenantId);

    List<RoleModel> listAll(@Param("excludeRoleIds") List<String> excludeRoleIds);

    List<RoleModel> listByOrgId(@Param("orgId") String orgId);

    RoleModel selectByIdent(@Param("ident") String ident);

    RoleModel selectByName(@Param("name") String name);

    List<RoleModel> listByNames(@Param("names") List<String> names);

    List<RoleDto> listWithUserCnt(@Param("roleName") String roleName, @Param("orders") List<String> orders);

}
