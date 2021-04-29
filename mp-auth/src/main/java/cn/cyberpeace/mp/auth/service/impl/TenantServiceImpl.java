package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.TenantModel;
import cn.cyberpeace.mp.auth.repository.TenantMapper;
import cn.cyberpeace.mp.auth.repository.condition.TenantCondition;
import cn.cyberpeace.mp.auth.service.TenantService;
import cn.cyberpeace.mp.auth.service.fields.TenantFields;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TenantServiceImpl extends BaseModelServiceImpl<TenantModel, TenantMapper, TenantFields>
        implements TenantService {

    public static final Class<TenantCondition> customConditionClass = TenantCondition.class;

    @Autowired
    public TenantServiceImpl(TenantMapper modelMapper, TenantFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public void loadFilterCondition(HumanCondition humanCondition, Map<String, Object> params) {
        Object name = params.getOrDefault("name", null);
        if (name != null) {
            humanCondition.filterEqual("name", name);
        }

        Object identifier = params.getOrDefault("identifier", null);
        if (identifier != null) {
            humanCondition.filterEqual("identifier", identifier);
        }
    }

}
