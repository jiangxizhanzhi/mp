package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowTaskConfModel;

import java.util.List;


public interface FlowTaskConfService extends BaseModelService<FlowTaskConfModel, QueryParams> {

    
    FlowTaskConfModel findByTaskId(String taskId);

    
    List<FlowTaskConfModel> listByTaskIds(List<String> taskIds);

}

