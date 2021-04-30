package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.repository.RoleMapper;
import cn.cyberpeace.mp.auth.service.RoleService;
import cn.cyberpeace.mp.auth.service.fields.RoleFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends BaseModelServiceImpl<RoleModel, RoleMapper, RoleFields>
        implements RoleService {

    @Autowired
    public RoleServiceImpl(RoleMapper modelMapper, RoleFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

}
