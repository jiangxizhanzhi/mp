package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.model.UserPermissionModel;
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
@ModelClass(UserPermissionModel.class)
public interface UserPermissionMapper extends BaseModelMapper<UserPermissionModel> {

    @Select("select distinct * from mp_auth_target where id in " +
            "(select target from mp_auth_user_permission where owner = #{userId})")
    List<TargetModel> listByUserId(@Param("userId") String userId);


    @Delete("delete from mp_auth_user_permission where owner = #{userId}")
    void deleteByUserId(@Param("userId") String userId);

    @Delete("delete from mp_auth_user_permission where target = #{targetId}")
    void deleteByTargetId(@Param("targetId") String targetId);

}
