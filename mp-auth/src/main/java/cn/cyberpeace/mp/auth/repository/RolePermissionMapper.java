package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.RolePermissionModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
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
@ModelClass(RolePermissionModel.class)
public interface RolePermissionMapper extends BaseModelMapper<RolePermissionModel> {

    List<TargetModel> listTargetsByRoleId(@Param("roleId") String roleId);

    void deleteByRoleId(@Param("roleId") String roleId);

    void deleteByTargetId(@Param("targetId") String targetId);

}
