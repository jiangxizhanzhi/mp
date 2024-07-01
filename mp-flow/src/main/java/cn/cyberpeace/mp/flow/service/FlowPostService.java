package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.flow.response.BatchActuatorResponse;

/**
 * 流量任务后处理逻辑
 */
public interface FlowPostService {
    /**
     * onFlowTaskError
     *
     * @param sceneId
     * @param nodeId
     * @param flowId
     * @param msg
     */
    void onFlowTaskError(String sceneId, String nodeId, String flowId, String msg);

    /**
     * onNodeFlowTaskError
     *
     * @param sceneId
     * @param nodeId
     * @param response
     */
    void onNodeFlowTaskError(String sceneId, String nodeId, BatchActuatorResponse response);

    /**
     * onFlowTaskOver
     *
     * @param sceneId
     * @param nodeId
     * @param flowId
     */
    void onFlowTaskOver(String sceneId, String nodeId, String flowId);

    /**
     * onStopTaskNode
     *
     * @param sceneId
     * @param nodeId
     */
    void onStopTaskNode(String sceneId, String nodeId);
}
