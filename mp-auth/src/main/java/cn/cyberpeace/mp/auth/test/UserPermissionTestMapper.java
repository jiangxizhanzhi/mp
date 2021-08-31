package cn.cyberpeace.mp.auth.test;

import cn.cyberpeace.mp.auth.base.repository.BasePermissionMapper;
import cn.cyberpeace.mp.auth.base.repository.stereotype.PermissionModelClass;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.model.UserPermissionModel;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Mapper
@Repository
@PermissionModelClass(permissionClass = UserPermissionModel.class, targetClass = TargetModel.class)
@ModelClass(UserPermissionModel.class)
public interface UserPermissionTestMapper extends BasePermissionMapper {

}
