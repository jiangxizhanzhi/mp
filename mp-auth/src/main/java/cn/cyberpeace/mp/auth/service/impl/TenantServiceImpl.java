package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.TenantModel;
import cn.cyberpeace.mp.auth.repository.TenantMapper;
import cn.cyberpeace.mp.auth.service.TenantService;
import cn.cyberpeace.mp.auth.service.fields.TenantFields;
import cn.cyberpeace.mp.auth.service.param.TenantQueryParams;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TenantServiceImpl extends BaseModelServiceImpl<TenantModel, TenantMapper, TenantFields, TenantQueryParams>
        implements TenantService {

    @Autowired
    public TenantServiceImpl(TenantMapper modelMapper, TenantFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public void loadListFilterCondition(HumanCondition humanCondition, TenantQueryParams queryParams) {
        String name = queryParams.getName();
        if (name != null) {
            humanCondition.filterEqual("name", name);
        }

        String identifier = queryParams.getIdentifier();
        if (identifier != null) {
            humanCondition.filterEqual("identifier", identifier);
        }
    }

}
