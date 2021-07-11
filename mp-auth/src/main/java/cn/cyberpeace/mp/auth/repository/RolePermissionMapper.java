package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.RolePermissionModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
@ModelClass(RolePermissionModel.class)
public interface RolePermissionMapper extends BaseModelMapper<RolePermissionModel> {

}
