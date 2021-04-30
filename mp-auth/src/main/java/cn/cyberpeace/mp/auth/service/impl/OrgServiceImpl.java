package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.repository.OrgMapper;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.fields.OrgFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrgServiceImpl extends BaseModelServiceImpl<OrgModel, OrgMapper, OrgFields>
        implements OrgService {

    @Autowired
    public OrgServiceImpl(OrgMapper modelMapper, OrgFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

}
