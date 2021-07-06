package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(UserRoleModel.class)
public interface UserRoleMapper extends BaseModelMapper<UserRoleModel> {

    @Select("select distinct * from mp_auth_role where id in " +
            "(select role from mp_auth_user_role where user = #{userId})")
    List<RoleModel> listByUserId(@Param("userId") String userId);


    @Delete("delete from mp_auth_user_role where user = #{userId}")
    void deleteByUser(@Param("userId") String userId);

}
