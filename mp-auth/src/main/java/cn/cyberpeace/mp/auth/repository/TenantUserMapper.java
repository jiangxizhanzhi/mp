package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.TenantUserModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(TenantUserModel.class)
public interface TenantUserMapper extends BaseModelMapper<TenantUserModel> {

}
