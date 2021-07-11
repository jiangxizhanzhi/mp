package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowProtocolModel;

import java.util.List;


public interface FlowProtocolService extends BaseModelService<FlowProtocolModel, QueryParams> {

    
    List<FlowProtocolModel> selectByFlowId(String flowId);

    
    void deleteByFlowId(String flowId);

    
    int deleteAll();

}

