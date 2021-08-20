package cn.cyberpeace.mp.devops.cluster.repository;

import cn.cyberpeace.mp.auth.base.repository.BasePermissionMapper;
import cn.cyberpeace.mp.auth.base.repository.stereotype.PermissionModelClass;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.devops.cluster.model.ImageAuthModel;
import cn.cyberpeace.mp.devops.cluster.model.ImageModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@PermissionModelClass(permissionClass = ImageAuthModel.class, targetClass = ImageModel.class)
@ModelClass(ImageModel.class)
public interface ImagePermissionMapper extends BasePermissionMapper {

}
