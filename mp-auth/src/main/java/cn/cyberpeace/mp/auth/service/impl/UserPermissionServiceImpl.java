package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.UserPermissionModel;
import cn.cyberpeace.mp.auth.repository.UserPermissionMapper;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import cn.cyberpeace.mp.auth.service.fields.UserPermissionFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserPermissionServiceImpl
        extends BaseModelServiceImpl<UserPermissionModel, UserPermissionMapper, UserPermissionFields>
        implements UserPermissionService {

    @Autowired
    public UserPermissionServiceImpl(UserPermissionMapper modelMapper, UserPermissionFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

}
