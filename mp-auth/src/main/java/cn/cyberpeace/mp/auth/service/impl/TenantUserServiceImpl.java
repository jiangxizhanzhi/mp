package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.TenantUserModel;
import cn.cyberpeace.mp.auth.repository.TenantUserMapper;
import cn.cyberpeace.mp.auth.service.TenantUserService;
import cn.cyberpeace.mp.auth.service.fields.TenantUserFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TenantUserServiceImpl
        extends BaseModelServiceImpl<TenantUserModel, TenantUserMapper, TenantUserFields, QueryParams>
        implements TenantUserService {

    @Autowired
    public TenantUserServiceImpl(TenantUserMapper modelMapper, TenantUserFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

}
