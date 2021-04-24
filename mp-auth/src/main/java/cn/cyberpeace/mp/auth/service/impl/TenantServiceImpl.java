package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.TenantModel;
import cn.cyberpeace.mp.auth.repository.TenantMapper;
import cn.cyberpeace.mp.auth.repository.condition.TenantCondition;
import cn.cyberpeace.mp.auth.service.TenantService;
import cn.cyberpeace.mp.auth.service.fields.TenantFields;
import cn.cyberpeace.mp.base.repository.condition.CustomCondition;
import cn.cyberpeace.mp.base.service.BaseModelServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TenantServiceImpl extends BaseModelServiceImpl<TenantModel, TenantMapper> implements TenantService {

    public static final Class<TenantFields> modelFieldsClass = TenantFields.class;

    @Autowired
    public TenantServiceImpl(TenantMapper modelMapper) {
        setModelMapper(modelMapper);
    }

    @Override
    public CustomCondition getCustomCondition(Map<String, Object> params) {
        return new TenantCondition().loadFromParams(params);
    }

}
