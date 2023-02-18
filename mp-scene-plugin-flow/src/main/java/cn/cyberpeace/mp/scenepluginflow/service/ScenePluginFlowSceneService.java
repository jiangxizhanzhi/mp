package cn.cyberpeace.mp.scenepluginflow.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import cn.cyberpeace.mp.flow.response.FlowRecordsResponse;
import cn.cyberpeace.mp.scene.core.bo.SceneContextBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.scenepluginflow.bo.base.Flow;
import cn.cyberpeace.mp.scenepluginflow.constant.PluginConstant;
import cn.cyberpeace.mp.scenepluginflow.plugin.FlowScenePluginHandler;
import cn.cyberpeace.mp.scenepluginflow.plugin.flow.FlowExecutor;
import cn.cyberpeace.mp.scenepluginflow.plugin.params.FlowIdWithSourceIp;
import cn.cyberpeace.mp.scenepluginflow.service.response.SceneFlowRecordsResponse;
import cn.cyberpeace.mp.scenepluginflow.util.SceneFlowCacheUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScenePluginFlowSceneService {

    @Resource
    private FlowExecutor flowExecutor;

    public ServiceResult<Boolean> startFlow(String sceneId, String nodeId, String flowId, String sourceIp, SceneContextBO context) {
        var sceneHandler = new SceneHandler(sceneId, null, false, context);
        CloudScenePluginHandler cloudPluginHandler = sceneHandler.getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        if (cloudPluginHandler != null && cloudPluginHandler.getElement(nodeId) != null && StringUtils.isEmpty(cloudPluginHandler.getElement(nodeId).getResourceId())) {
            throw new MpRuntimeException(FlowErrorCode.NODE_NOT_START);
        }
        FlowScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        var result = pluginHandler.startFlow(nodeId, flowId, sourceIp, true);
        return new ServiceResult<>(result);
    }

    public ServiceResult<Boolean> stopFlow(String sceneId, String nodeId, String flowId, SceneContextBO context) {
        var sceneHandler = new SceneHandler(sceneId, null, false, context);
        FlowScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        var result = pluginHandler.stopFlow(nodeId, flowId);
        return new ServiceResult<>(result);
    }

    @Transactional
    public ServiceResult<Boolean> updateFlowParams(String sceneId, String nodeId, String flowId, String sourceIp, SceneContextBO context) {
        var sceneHandler = new SceneHandler(sceneId, null, false, context);
        FlowScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        var flow = pluginHandler.updateFlowParams(nodeId, flowId, sourceIp);
        if (flow != null && flow.getTaskId() != null) {
            FlowTaskModel model = new FlowTaskModel();
            model.setId(flow.getTaskId());
            model.setOriginIps(sourceIp);
            flowExecutor.updateFlowTaskInfo(model);
        }
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> startFlows(String sceneId, String nodeId, List<FlowIdWithSourceIp> flowIdWithSourceIps,
                                             SceneContextBO context, int runType) {
        var sceneHandler = new SceneHandler(sceneId, null, false, context);

        CloudScenePluginHandler cloudPluginHandler = sceneHandler.getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        if (cloudPluginHandler != null && cloudPluginHandler.getElement(nodeId) != null && StringUtils.isEmpty(cloudPluginHandler.getElement(nodeId).getResourceId())) {
            throw new MpRuntimeException(FlowErrorCode.NODE_NOT_START);
        }

        FlowScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        if (pluginHandler == null) {
            return new ServiceResult<>(true);
        }
        var result = pluginHandler.startFlows(nodeId, flowIdWithSourceIps, true, runType);
        return new ServiceResult<>(result);
    }

    public ServiceResult<Boolean> stopFlows(String sceneId, String nodeId, List<String> flowIds, SceneContextBO context) {
        var sceneHandler = new SceneHandler(sceneId, null, false, context);
        FlowScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        if (pluginHandler == null) {
            return new ServiceResult<>(true);
        }
        var result = pluginHandler.stopFlows(nodeId, flowIds);
        return new ServiceResult<>(result);
    }

    public ServiceResult<SceneFlowRecordsResponse> queryFlowRecords(String sceneId, String nodeId, String flowId,
                                                                    SceneContextBO context, Boolean isBatch) {
        var sceneHandler = new SceneHandler(sceneId, null, false, context);
        FlowScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        if (pluginHandler == null) {
            return new ServiceResult<>();
        }
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
        if (flow == null && !isBatch) {
            return new ServiceResult<>();
        }
        ServiceResult<FlowRecordsResponse> result;
        if (Boolean.TRUE.equals(isBatch)) {
            result = flowExecutor.queryFlowRecords(flowElement.getSrcElement().getEid());
        } else {
            result = flowExecutor.queryFlowRecords(flow.getTaskId());
        }
        SceneFlowRecordsResponse response = new SceneFlowRecordsResponse();
        if (result.judgeSuccess()) {
            response.setResult(result.getData().getResult());
            response.setStatus(result.getData().getStatus());
            response.setFlowRecordsList(result.getData().getFlowRecordsList());
            return new ServiceResult<>(response);
        } else {
            return new ServiceResult<>(null, result.getServiceCode(), result.getDetail());
        }
    }

}
