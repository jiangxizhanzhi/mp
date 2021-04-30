package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.OrgPermissionModel;
import cn.cyberpeace.mp.auth.repository.OrgPermissionMapper;
import cn.cyberpeace.mp.auth.service.OrgPermissionService;
import cn.cyberpeace.mp.auth.service.fields.OrgPermissionFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrgPermissionServiceImpl
        extends BaseModelServiceImpl<OrgPermissionModel, OrgPermissionMapper, OrgPermissionFields>
        implements OrgPermissionService {

    @Autowired
    public OrgPermissionServiceImpl(OrgPermissionMapper modelMapper, OrgPermissionFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

}
