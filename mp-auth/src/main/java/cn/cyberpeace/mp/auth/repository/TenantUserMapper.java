package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.TenantUserModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface TenantUserMapper extends BaseModelMapper<TenantUserModel> {
    Class<?> modelClass = TenantUserModel.class;
}
