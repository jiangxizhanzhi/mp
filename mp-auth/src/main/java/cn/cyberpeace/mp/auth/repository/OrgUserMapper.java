package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.OrgUserModel;
import cn.cyberpeace.mp.auth.model.UserModel;
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
@ModelClass(OrgUserModel.class)
public interface OrgUserMapper extends BaseModelMapper<OrgUserModel> {


    @Select("select distinct * from mp_auth_user where id in " +
            "(select user from mp_auth_org_user where org = #{orgId})")
    List<UserModel> listUsersByOrgId(@Param("orgId") String orgId);

    @Select("select distinct * from mp_auth_org where id in " +
            "(select org from mp_auth_org_user where user = #{userId})")
    List<OrgModel> listOrgsByUserId(@Param("userId") String userId);


    /**
     *
     * @param orgId
     * @param username 会同时模糊查询username和name
     * @param userIds
     * @return
     */
    @Select("<script>" +
            "select * from mp_auth_user " +
            "<where>" +
            "  <if test=\"orgId != null and orgId != ''\">" +
            "    AND id in (select user from mp_auth_org_user where org = #{orgId})" +
            "  </if>" +
            "  <if test=\"userIds != null\"> " +
            "    AND id in " +
            "    <foreach item=\"userId\" index=\"index\" collection=\"userIds\" open=\"(\" separator=\",\" close=\")\"> " +
            "      #{userId} " +
            "    </foreach>" +
            "  </if>" +
            "  <if test=\"username != null and username != ''\">" +
            "    AND ( username like CONCAT('%', #{username}, '%') or name like CONCAT('%', #{username}, '%')) " +
            "  </if>" +
            "order by username " +
            "</where></script>")
    List<UserModel> listUsers(@Param("orgId") String orgId, @Param("username") String username, @Param("userIds") List<String> userIds);


    @Delete("delete from mp_auth_org_user where user = #{userId}")
    void deleteByUserId(@Param("userId") String userId);

    @Delete("delete from mp_auth_org_user where org = #{orgId}")
    void deleteByOrgId(@Param("orgId") String orgId);
}
