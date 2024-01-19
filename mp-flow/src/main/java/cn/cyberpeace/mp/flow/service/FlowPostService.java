package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.flow.response.BatchActuatorCallbackResponse;

public interface FlowPostService {
    void onTaskError(String sceneId, String nodeId, String flowId, String msg);

    void onSceneTaskError(String sceneId, String nodeId, BatchActuatorCallbackResponse batchActuatorCallbackResponse);
}
