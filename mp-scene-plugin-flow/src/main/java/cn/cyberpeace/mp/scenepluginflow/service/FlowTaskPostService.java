 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowTaskPostService.java.
 // File size 4346.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scenepluginflow.service;

import cn.cyberpeace.mp.flow.response.BatchActuatorResponse;
import cn.cyberpeace.mp.flow.service.FlowPostService;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scenepluginflow.bo.base.Flow;
import cn.cyberpeace.mp.scenepluginflow.util.SceneFlowCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 流量任务后处理逻辑
 * 负责接受到上报流量消息后的处理逻辑
 */
@Slf4j
@Service
public class FlowTaskPostService implements FlowPostService {
    /**
     * getSceneHandler
     *
     * @param sceneId
     * @return 场景实例的处理
     */
    private SceneHandler getSceneHandler(String sceneId) {
        return SceneFlowCacheUtil.getSceneHandler(sceneId);
    }

    /**
     * getFlow
     *
     * @param sceneId
     * @param nodeId
     * @param flowId
     * @return
     */
    private Flow getFlow(String sceneId, String nodeId, String flowId){
        List<Flow> flows = SceneFlowCacheUtil.get(sceneId, nodeId);
        Optional<Flow> flow = flows.stream().filter(f -> Objects.equals(f.getId(), flowId)).findFirst();
        return flow.orElse(null);
    }

    /**
     * 单个任务执行异常时的回调处理
     *
     * @param sceneId   场景
     * @param nodeId 节点
     * @param flowId 流量
     * @param msg 错误消息
     */
    public void onFlowTaskError(String sceneId, String nodeId, String flowId, String msg){
        Flow flow = getFlow(sceneId, nodeId, flowId);
        errorFLow(flow, msg);
        onStopTaskNode(sceneId, nodeId);
    }

    /**
     * 流量任务出错时，节点级别的后处理
     *
     * @param sceneId   场景
     * @param nodeId 节点
     * @param response 执行信息
     */
    public void onNodeFlowTaskError(String sceneId, String nodeId, BatchActuatorResponse response){
        List<Flow> flows = SceneFlowCacheUtil.get(sceneId, nodeId);
        Map<String, Flow> flowMap = flows.stream()
                .collect(Collectors.toMap(Flow::getId, Function.identity()));

        log.info("batch tasks run callbackConsumer");
        response.getErrorTaskModels().forEach(flowTaskModel -> {
            errorFLow(flowMap.get(flowTaskModel.getFlowId()), "Flow execute error");
        });
        response.getSuccessTaskModels().forEach(flowTaskModel -> {
            successOffFLow(flowMap.get(flowTaskModel.getFlowId()));
        });
    }

    /**
     * 单个任务执行时的回调处理
     *
     * @param sceneId   场景
     * @param nodeId 节点
     * @param flowId 流量
     */
    public void onFlowTaskOver(String sceneId, String nodeId, String flowId){
        Flow flow = getFlow(sceneId, nodeId, flowId);
        successOffFLow(flow);
        onStopTaskNode(sceneId, nodeId);
    }

    /**
     * successOffFLow
     *
     * @param flow
     */
    private void successOffFLow(Flow flow) {
        log.info("flow success off,flowId is {},taskId is {}", flow.getId(), flow.getTaskId());
        flow.setStatus(Flow.Status.OFF);
        flow.setStatusText(null);
    }

    /**
     * errorFLow
     *
     * @param flow
     * @param error
     */
    private void errorFLow(Flow flow, String error) {
        log.info("flow error,flowId is {},taskId is {},error is {}", flow.getId(), flow.getTaskId(), error);
        flow.setStatus(Flow.Status.ERROR);
        flow.setStatusText(error);
    }

    // FlowScenePluginHandler.java:232
    /**
     * 单个节点上流量任务停止时的后处理操作
     *
     * @param sceneId
     * @param nodeId
     */
    public void onStopTaskNode(String sceneId, String nodeId){
        SceneHandler handler = getSceneHandler(sceneId);
        handler.asyncAckElementChanged(nodeId, true, true);
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
            handler.updateSceneElementWithPlugins(nodeId);
        }
    }
}
