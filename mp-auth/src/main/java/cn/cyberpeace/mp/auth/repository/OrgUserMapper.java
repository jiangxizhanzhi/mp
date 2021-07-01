package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.OrgUserModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(OrgUserModel.class)
public interface OrgUserMapper extends BaseModelMapper<OrgUserModel> {


    @Select("select * from mp_auth_user where id in " +
            "(select user from mp_auth_org_user where org = #{orgId})")
    List<UserModel> listUsersByOrgId(@Param("orgId") String orgId);

    @Select("select * from mp_auth_org where id in " +
            "(select org from mp_auth_org_user where user = #{userId})")
    List<OrgModel> listOrgsByUserId(@Param("userId") String userId);


    @Select("<script>" +
            "select * from mp_auth_user " +
            "<where>" +
            "  <if test=\"orgId != null\">" +
            "    AND id in (select user from mp_auth_org_user where org = #{orgId})" +
            "  </if>" +
            "  <if test=\"username != null and username != ''\">" +
            "    AND username like CONCAT('%', #{username}, '%')" +
            "  </if>" +
            "</where></script>")
    List<UserModel> listUsers(@Param("orgId") String orgId, @Param("username") String username);

}
