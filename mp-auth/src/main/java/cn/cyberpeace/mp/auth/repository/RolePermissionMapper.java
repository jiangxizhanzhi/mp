package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.dto.target.TargetRoleDto;
import cn.cyberpeace.mp.auth.model.RolePermissionModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
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
@ModelClass(RolePermissionModel.class)
public interface RolePermissionMapper extends BaseModelMapper<RolePermissionModel> {

    List<TargetModel> listTargetsByRoleIds(@Param("tenant") String tenant, @Param("roleIds") List<String> roleIds);

    List<TargetRoleDto> listTargetRoleDtos(@Param("roleIds") List<String> roleIds);


}
