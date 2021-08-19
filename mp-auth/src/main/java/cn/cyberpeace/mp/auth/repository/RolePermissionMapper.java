package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.base.repository.BasePermissionMapper;
import cn.cyberpeace.mp.auth.model.RolePermissionModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(RolePermissionModel.class)
public interface RolePermissionMapper extends BasePermissionMapper<RolePermissionModel, TargetModel>, BaseModelMapper {

    List<TargetModel> listTargetsByRoleId(@Param("roleId") String roleId);

}
