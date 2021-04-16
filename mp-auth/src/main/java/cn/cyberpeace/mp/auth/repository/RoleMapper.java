package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface RoleMapper extends BaseModelMapper<RoleModel> {
}
