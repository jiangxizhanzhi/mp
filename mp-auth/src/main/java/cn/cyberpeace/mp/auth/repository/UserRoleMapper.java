package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.dto.role.UserRoleStatistic;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserModel;
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

    @Select("<script>" +
            "select distinct * from mp_auth_role where id in " +
            "(select role from mp_auth_user_role where user = #{userId})" +
            "  <if test=\"excludeRoleIds != null and excludeRoleIds.size() > 0\">" +
            "    AND id not in " +
            "       <foreach item=\"roleId\" index=\"index\" collection=\"excludeRoleIds\" open=\"(\" separator=\",\" close=\")\"> " +
            "         #{roleId} " +
            "       </foreach>" +
            "  </if>" +
            "</script>")
    List<RoleModel> listByUserId(@Param("userId") String userId, @Param("excludeRoleIds") List<String> excludeRoleIds);


    @Select("<script>" +
            "select distinct * from mp_auth_role where id in " +
            "   (select role from mp_auth_user_role where user in " +
            "       <foreach item=\"userId\" index=\"index\" collection=\"userIds\" open=\"(\" separator=\",\" close=\")\"> " +
            "           #{userId} " +
            "       </foreach>" +
            "   )" +
            "  <if test=\"excludeRoleIds != null and excludeRoleIds.size() > 0\">" +
            "    AND id not in " +
            "       <foreach item=\"roleId\" index=\"index\" collection=\"excludeRoleIds\" open=\"(\" separator=\",\" close=\")\"> " +
            "         #{roleId} " +
            "       </foreach>" +
            "  </if>" +
            "</script>")
    List<RoleModel> listByUserIds(@Param("userIds") List<String> userIds, @Param("excludeRoleIds") List<String> excludeRoleIds);


    @Select("select distinct * from mp_auth_user where id in " +
            "(select user from mp_auth_user_role where role = #{roleId})")
    List<UserModel> listByRoleId(@Param("roleId") String roleId);


    @Select("select tabA.rid roleId, tabR.name roleName, tabA.cnt userCnt " +
            "from (select role rid, count(user) cnt from mp_auth_user_role group by role) as tabA " +
            "         left join mp_auth_role tabR on tabA.rid = tabR.id " +
            "order by userCnt desc")
    List<UserRoleStatistic> getUserRoleStatistics();


    @Delete("delete from mp_auth_user_role where user = #{userId}")
    int deleteByUserId(@Param("userId") String userId);

    @Delete("delete from mp_auth_user_role where role = #{roleId}")
    int deleteByRoleId(@Param("roleId") String roleId);

    @Delete("<script>" +
            "delete from mp_auth_user_role where role = #{roleId} " +
            "  <if test=\"userIds != null and userIds.size() > 0\"> " +
            "    AND user IN " +
            "    <foreach item=\"userId\" index=\"index\" collection=\"userIds\" open=\"(\" separator=\",\" close=\")\"> " +
            "      #{userId} " +
            "    </foreach>" +
            "  </if>" +
            "</script>")
    int deleteByRoleIdAndUserIds(@Param("roleId") String roleId, @Param("userIds") List<String> userIds);

}
