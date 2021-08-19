package cn.cyberpeace.mp.auth.test;

import cn.cyberpeace.mp.auth.base.repository.BasePermissionMapper;
import cn.cyberpeace.mp.auth.base.repository.stereotype.PermissionModelClass;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.model.UserPermissionModel;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@PermissionModelClass(permissionClass = UserPermissionModel.class, targetClass = TargetModel.class)
@ModelClass(TargetModel.class)
public interface UserPermissionTestMapper extends BasePermissionMapper<UserPermissionModel, TargetModel> {

}
