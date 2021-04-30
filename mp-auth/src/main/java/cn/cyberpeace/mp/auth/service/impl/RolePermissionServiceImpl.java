package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.RolePermissionModel;
import cn.cyberpeace.mp.auth.repository.RolePermissionMapper;
import cn.cyberpeace.mp.auth.service.RolePermissionService;
import cn.cyberpeace.mp.auth.service.fields.RolePermissionFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolePermissionServiceImpl
        extends BaseModelServiceImpl<RolePermissionModel, RolePermissionMapper, RolePermissionFields>
        implements RolePermissionService {

    @Autowired
    public RolePermissionServiceImpl(RolePermissionMapper modelMapper, RolePermissionFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

}
