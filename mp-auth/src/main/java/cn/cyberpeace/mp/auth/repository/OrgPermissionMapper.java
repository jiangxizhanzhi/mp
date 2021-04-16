package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.OrgPermissionModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface OrgPermissionMapper extends BaseModelMapper<OrgPermissionModel> {
}
