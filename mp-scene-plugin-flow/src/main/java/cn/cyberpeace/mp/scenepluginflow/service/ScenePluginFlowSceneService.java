package cn.cyberpeace.mp.scenepluginflow.service;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.response.FlowRecordsResponse;
import cn.cyberpeace.mp.flow.service.impl.FlowTaskServiceImpl;
import cn.cyberpeace.mp.scene.core.bo.SceneContextBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scenepluginflow.bo.base.Flow;
import cn.cyberpeace.mp.scenepluginflow.plugin.params.FlowIdWithSourceIp;
import cn.cyberpeace.mp.scenepluginflow.constant.PluginConstant;
import cn.cyberpeace.mp.scenepluginflow.plugin.FlowScenePluginHandler;
import cn.cyberpeace.mp.scenepluginflow.service.response.SceneFlowRecordsResponse;
import cn.cyberpeace.mp.scenepluginflow.util.SceneFlowCacheUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScenePluginFlowSceneService {

    @Resource
    private FlowTaskServiceImpl flowService3;

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

    public ServiceResult<Boolean> startFlows(String sceneId, String nodeId, List<FlowIdWithSourceIp> flowIdWithSourceIps,
                                             SceneContextBO context, int runType) {
        var sceneHandler = new SceneHandler(sceneId, null, false, context);
        FlowScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        var result = pluginHandler.startFlows(nodeId, flowIdWithSourceIps, true, runType);
        return new ServiceResult<>(result);
    }

    public ServiceResult<Boolean> stopFlows(String sceneId, String nodeId, List<String> flowIds, SceneContextBO context) {
        var sceneHandler = new SceneHandler(sceneId, null, false, context);
        FlowScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        var result = pluginHandler.stopFlows(nodeId, flowIds);
        return new ServiceResult<>(result);
    }

    public ServiceResult<SceneFlowRecordsResponse> queryFlowRecords(String sceneId, String nodeId, String flowId,
                                                                    SceneContextBO context, Boolean isBatch) {
        var sceneHandler = new SceneHandler(sceneId, null, false, context);
        FlowScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        var flowElement = pluginHandler.getElement(nodeId);
        if (flowElement == null) {
            return new ServiceResult<>();
        }

        List<Flow> flows = SceneFlowCacheUtil.get(pluginHandler.getSourceSceneId(), nodeId);
        flowElement.setFlows(flows);
        if (flows == null) {
            return new ServiceResult<>();
        }

        var flowMap = flows.stream().collect(Collectors.toMap(Flow::getId, flow -> flow));
        var flow = flowMap.getOrDefault(flowId, null);
        if (flow == null) {
            return new ServiceResult<>();
        }
        ServiceResult<FlowRecordsResponse> result = new ServiceResult<>();
        if (isBatch) {
            result = flowService3.queryFlowRecords(nodeId);
        } else {
            result = flowService3.queryFlowRecords(flow.getTaskId());
        }
        SceneFlowRecordsResponse response = new SceneFlowRecordsResponse();
        response.setFlowRecordsList(result.getData().getFlowRecordsList());
        return new ServiceResult<>(response);
    }

}
