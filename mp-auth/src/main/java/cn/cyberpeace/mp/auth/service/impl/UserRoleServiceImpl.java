package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.auth.repository.UserRoleMapper;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.service.fields.UserRoleFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl extends BaseModelServiceImpl<UserRoleModel, UserRoleMapper, UserRoleFields>
        implements UserRoleService {

    @Autowired
    public UserRoleServiceImpl(UserRoleMapper modelMapper, UserRoleFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

}
