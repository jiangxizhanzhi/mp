package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.repository.TargetMapper;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.auth.service.fields.TargetFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TargetServiceImpl extends BaseModelServiceImpl<TargetModel, TargetMapper, TargetFields>
        implements TargetService {

    @Autowired
    public TargetServiceImpl(TargetMapper modelMapper, TargetFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

}
