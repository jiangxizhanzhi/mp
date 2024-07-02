package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.flow.response.BatchActuatorResponse;

/**
 * @ClassName FlowPostService
 * @Description 流量任务后处理逻辑
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
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
