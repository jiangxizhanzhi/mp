package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowTaskPortModel;


public interface FlowTaskPortService extends BaseModelService<FlowTaskPortModel, QueryParams> {

    
    FlowTaskPortModel findByNetworkIdAndActuatorId(String networkId, String actuatorId);

}

