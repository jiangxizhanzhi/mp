package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.bo.AddFlowTaskTaskInfo;
import cn.cyberpeace.mp.flow.bo.FlowTaskNode;
import cn.cyberpeace.mp.flow.model.FlowTaskTaskModel;
import cn.cyberpeace.mp.flow.request.queryparam.FlowTaskPageQueryParams;

import java.util.List;


public interface FlowTaskTaskService extends BaseModelService<FlowTaskTaskModel, QueryParams> {

    
    int updateTaskStatus(String taskId, int taskStatus);

    
    int batchUpdateTaskStatus(List<String> taskIds, int taskStatus);

    
    List<FlowTaskTaskModel> listByTaskIds(List<String> taskIds);

    
    List<FlowTaskNode> nodeList(String sceneId);

    
    void addTask(List<AddFlowTaskTaskInfo> details, String sceneId, String actuatorId, boolean isAsync, List<FlowTaskTaskModel> flowTaskTaskModelList);

    
    List<FlowTaskTaskModel> listByNodeIdAndFlowId(String nodeId, String flowId);

    
    List<FlowTaskTaskModel> listByNodeIdAndSceneId(String nodeId, String sceneId);

    
    void deleteByNodeIdAndSceneId(String nodeId, String sceneId);

    
    List<FlowTaskTaskModel> listBySceneIdAndStatus(String nodeId, int taskStatus);

    
    List<FlowTaskTaskModel> listBySceneId(String sceneId);

    
    List<FlowTaskTaskModel> listByCondition(FlowTaskPageQueryParams request);

}

