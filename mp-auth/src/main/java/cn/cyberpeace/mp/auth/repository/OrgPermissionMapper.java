package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.OrgPermissionModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(OrgPermissionModel.class)
public interface OrgPermissionMapper extends BaseModelMapper<OrgPermissionModel> {

}
