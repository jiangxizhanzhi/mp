package cn.cyberpeace.mp.scenepluginflow.service;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.scene.core.bo.SceneContextBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scenepluginflow.constant.PluginConstant;
import cn.cyberpeace.mp.scenepluginflow.plugin.FlowScenePluginHandler;
import org.springframework.stereotype.Service;

@Service
public class ScenePluginFlowSceneService {

    public ServiceResult<Boolean> startFlow(String sceneId, String nodeId, String flowId, SceneContextBO context) {
        var sceneHandler = new SceneHandler(sceneId, null, false, context);
        FlowScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        var result = pluginHandler.startFlow(nodeId, flowId, true);
        return new ServiceResult<>(result);
    }

    public ServiceResult<Boolean> stopFlow(String sceneId, String nodeId, String flowId, SceneContextBO context) {
        var sceneHandler = new SceneHandler(sceneId, null, false, context);
        FlowScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        var result = pluginHandler.stopFlow(nodeId, flowId);
        return new ServiceResult<>(result);
    }

}
