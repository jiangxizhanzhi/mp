package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(RoleModel.class)
public interface RoleMapper extends BaseModelMapper<RoleModel> {

    @Select("select * from mp_auth_role where tenant = #{tenantId}")
    List<RoleModel> listByTenantId(@Param("tenantId") String tenantId);

}
