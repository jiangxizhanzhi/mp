package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.dto.role.RoleDto;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(RoleModel.class)
public interface RoleMapper extends BaseModelMapper<RoleModel> {

    @Select("select * from mp_auth_role where tenant = #{tenantId}")
    List<RoleModel> listByTenantId(@Param("tenantId") String tenantId);


    @Select("select * from mp_auth_role")
    List<RoleModel> listAll();


    @Select("select * from mp_auth_role where org = #{orgId}")
    List<RoleModel> listByOrgId(@Param("orgId") String orgId);


    @Select("select * from mp_auth_role where identifier = #{ident}")
    RoleModel selectByIdent(@Param("ident") String ident);

    @Select("select * from mp_auth_role where name = #{name}")
    RoleModel selectByName(@Param("name") String name);


    @Select("<script>" +
            "select * from mp_auth_role where name in " +
            "       <foreach item=\"name\" index=\"index\" collection=\"names\" open=\"(\" separator=\",\" close=\")\"> " +
            "           #{name} " +
            "       </foreach>" +
            "</script>")
    List<RoleModel> listByNames(@Param("names") List<String> names);

    @Select("<script>" +
            "select r.*, (select count(distinct user) from mp_auth_user_role ru where r.id = ru.role) cnt from mp_auth_role r " +
            "<where>" +
            "  <if test=\"roleName != null and roleName != ''\">" +
            "    AND r.name like CONCAT('%', #{roleName}, '%')" +
            "  </if>" +
            "</where></script>")
    @Results({
            @Result(column = "cnt", property = "userCnt"),
    })
    List<RoleDto> listWithUserCnt(@Param("roleName") String roleName);

}
