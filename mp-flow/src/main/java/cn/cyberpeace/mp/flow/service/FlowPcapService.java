package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowPcapModel;

import java.util.List;


public interface FlowPcapService extends BaseModelService<FlowPcapModel, QueryParams> {

    
    int countAll();

    
    int deleteAll();

    
    FlowPcapModel getByFlowId(String flowId);

    
    List<FlowPcapModel> listAll();

}

