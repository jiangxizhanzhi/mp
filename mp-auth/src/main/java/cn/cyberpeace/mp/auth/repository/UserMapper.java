package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(UserModel.class)
public interface UserMapper extends BaseModelMapper<UserModel> {

    @Select("select count(id) from mp_auth_user")
    int getUserCnt();


    @Update("update mp_auth_user set identifier = null where id = #{id}")
    int updateIdentToNull(@Param("id") String id);

}
