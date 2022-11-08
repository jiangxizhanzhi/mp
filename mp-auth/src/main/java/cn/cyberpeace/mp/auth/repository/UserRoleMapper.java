package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.dto.role.UserRoleStatistic;
import cn.cyberpeace.mp.auth.dto.user.UserOrgRoleDto;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserRoleModel;
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
@ModelClass(UserRoleModel.class)
public interface UserRoleMapper extends BaseModelMapper<UserRoleModel> {

    List<RoleModel> listRolesByUserId(@Param("userId") String userId);

    List<RoleModel> listRolesByUserIds(@Param("userIds") List<String> userIds);

    List<UserOrgRoleDto> listUsersByRoleId(@Param("roleId") String roleId);

    List<UserRoleStatistic> getUserRoleStatistics();

}
