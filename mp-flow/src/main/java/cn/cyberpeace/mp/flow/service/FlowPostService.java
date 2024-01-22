package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.flow.response.BatchActuatorCallbackResponse;

/**
 * 流量任务后处理逻辑
 */
public interface FlowPostService {
    void onFlowTaskError(String sceneId, String nodeId, String flowId, String msg);

    void onNodeFlowTaskError(String sceneId, String nodeId, BatchActuatorCallbackResponse response);

    void onFlowTaskOver(String sceneId, String nodeId, String flowId);

    void onStopTaskNode(String sceneId, String nodeId);
}
