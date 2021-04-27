package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserRoleMapper extends BaseModelMapper<UserRoleModel> {
    Class<?> modelClass = UserRoleModel.class;
}
