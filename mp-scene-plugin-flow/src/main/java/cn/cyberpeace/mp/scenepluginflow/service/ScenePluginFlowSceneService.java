 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ScenePluginFlowSceneService.java.
 // File size 8234.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scenepluginflow.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import cn.cyberpeace.mp.flow.response.FlowRecordsResponse;
import cn.cyberpeace.mp.scene.core.bo.SceneContextBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneElementBO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ScenePluginFlowSceneService {

    @Resource
    private FlowExecutor flowExecutor;

    /**
     * startFlow
     *
     * @param sceneId
     * @param nodeId
     * @param flowId
     * @param sourceIp
     * @param context
     * @return
     */
    public ServiceResult<Boolean> startFlow(String sceneId, String nodeId, String flowId, String sourceIp, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, context);
        CloudScenePluginHandler cloudPluginHandler = sceneHandler.getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        if (cloudPluginHandler != null && cloudPluginHandler.getElement(nodeId) != null && StringUtils.isEmpty(cloudPluginHandler.getElement(nodeId).getResourceId())) {
            throw new MpRuntimeException(FlowErrorCode.NODE_NOT_START);
        }
        FlowScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        boolean result = pluginHandler.startFlow(nodeId, flowId, sourceIp, true);
        return new ServiceResult<>(result);
    }

    /**
     * stopFlow
     *
     * @param sceneId
     * @param nodeId
     * @param flowId
     * @param context
     * @return
     */
    public ServiceResult<Boolean> stopFlow(String sceneId, String nodeId, String flowId, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, context);
        FlowScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        boolean result = pluginHandler.stopFlow(nodeId, flowId);
        return new ServiceResult<>(result);
    }

    /**
     * updateFlowParams
     *
     * @param sceneId
     * @param nodeId
     * @param flowId
     * @param sourceIp
     * @param context
     * @return
     */
    @Transactional
    public ServiceResult<Boolean> updateFlowParams(String sceneId, String nodeId, String flowId, String sourceIp, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, context);
        FlowScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        Flow flow = pluginHandler.updateFlowParams(nodeId, flowId, sourceIp);
        if (flow != null && flow.getTaskId() != null) {
            FlowTaskModel model = new FlowTaskModel();
            model.setId(flow.getTaskId());
            model.setOriginIps(sourceIp);
            flowExecutor.updateFlowTaskInfo(model);
        }
        return new ServiceResult<>(true);
    }

    /**
     * startFlows
     *
     * @param sceneId
     * @param nodeId
     * @param flowIdWithSourceIps
     * @param context
     * @param runType
     * @return
     */
    public ServiceResult<Boolean> startFlows(String sceneId, String nodeId, List<FlowIdWithSourceIp> flowIdWithSourceIps,
                                             SceneContextBO context, int runType) {
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, context);

        CloudScenePluginHandler cloudPluginHandler = sceneHandler.getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        if (cloudPluginHandler != null && cloudPluginHandler.getElement(nodeId) != null && StringUtils.isEmpty(cloudPluginHandler.getElement(nodeId).getResourceId())) {
            throw new MpRuntimeException(FlowErrorCode.NODE_NOT_START);
        }

        FlowScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        if (pluginHandler == null) {
            return new ServiceResult<>(true);
        }
        boolean result = pluginHandler.startFlows(nodeId, flowIdWithSourceIps, true, runType);
        return new ServiceResult<>(result);
    }

    /**
     * stopFlows
     *
     * @param sceneId
     * @param nodeId
     * @param flowIds
     * @param context
     * @return
     */
    public ServiceResult<Boolean> stopFlows(String sceneId, String nodeId, List<String> flowIds, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, context);
        FlowScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        if (pluginHandler == null) {
            return new ServiceResult<>(true);
        }
        boolean result = pluginHandler.stopFlows(nodeId, flowIds);
        return new ServiceResult<>(result);
    }

    /**
     * queryFlowRecords
     *
     * @param sceneId
     * @param nodeId
     * @param flowId
     * @param context
     * @param isBatch
     * @return
     */
    public ServiceResult<SceneFlowRecordsResponse> queryFlowRecords(String sceneId, String nodeId, String flowId,
                                                                    SceneContextBO context, Boolean isBatch) {
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, context);
        FlowScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        if (pluginHandler == null) {
            return new ServiceResult<>();
        }
        FlowPluginSceneElementBO flowElement = pluginHandler.getElement(nodeId);
        if (flowElement == null) {
            return new ServiceResult<>();
        }

        List<Flow> flows = SceneFlowCacheUtil.get(pluginHandler.getSourceSceneId(), nodeId);
        flowElement.setFlows(flows);
        if (flows == null) {
            return new ServiceResult<>();
        }

        Map<String, Flow> flowMap = new HashMap<>();
        for (Flow flow1 : flows) {
            if (flowMap.put(flow1.getId(), flow1) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }
        Flow flow = flowMap.getOrDefault(flowId, null);
        if (flow == null && !isBatch) {
            return new ServiceResult<>();
        }
        ServiceResult<FlowRecordsResponse> result;
        if (Boolean.TRUE.equals(isBatch)) {
            result = flowExecutor.queryBatchFlowRecords(sceneId);
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
