package cn.cyberpeace.mp.scenepluginflow.service;

import cn.cyberpeace.mp.flow.response.BatchActuatorCallbackResponse;
import cn.cyberpeace.mp.flow.service.FlowPostService;
import cn.cyberpeace.mp.flow.util.FlowCacheUtil;
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
    private SceneHandler getSceneHandler(String sceneId) {
        return FlowCacheUtil.sceneHandlerCache.getIfPresent(FlowCacheUtil.CONSUMER_PREFIX+sceneId);
    }

    private Flow getFlow(String sceneId, String nodeId, String flowId){
        List<Flow> flows = SceneFlowCacheUtil.get(sceneId, nodeId);
        Optional<Flow> flow = flows.stream().filter(f -> Objects.equals(f.getId(), flowId)).findFirst();
        return flow.orElse(null);
    }

    /**
     * 单个任务执行异常时的回调处理
     * @param sceneId   场景
     * @param nodeId 节点
     * @param flowId 流量
     * @param msg 错误消息
     */
    public void onTaskError(String sceneId, String nodeId, String flowId, String msg){
        Flow flow = getFlow(sceneId, nodeId, flowId);
        errorFLow(flow, msg);
        stopTaskNode(sceneId, nodeId);
    }

    /**
     * 流量任务出错时，节点级别的后处理
     * @param sceneId   场景
     * @param nodeId 节点
     * @param batchActuatorCallbackResponse 执行信息
     */
    public void onSceneTaskError(String sceneId, String nodeId, BatchActuatorCallbackResponse batchActuatorCallbackResponse){
        List<Flow> flows = SceneFlowCacheUtil.get(sceneId, nodeId);
        Map<String, Flow> flowMap = flows.stream()
                .collect(Collectors.toMap(Flow::getId, Function.identity()));

        log.info("batch tasks run callbackConsumer");
        batchActuatorCallbackResponse.getErrorTaskModels().forEach(flowTaskModel -> {
            errorFLow(flowMap.get(flowTaskModel.getFlowId()), "Flow execute error");
        });
        batchActuatorCallbackResponse.getSuccessTaskModels().forEach(flowTaskModel -> {
            successOffFLow(flowMap.get(flowTaskModel.getFlowId()));
        });
    }

    private void successOffFLow(Flow flow) {
        log.info("flow success off,flowId is {},taskId is {}", flow.getId(), flow.getTaskId());
        flow.setStatus(Flow.Status.OFF);
        flow.setStatusText(null);
    }

    private void errorFLow(Flow flow, String error) {
        log.info("flow error,flowId is {},taskId is {},error is {}", flow.getId(), flow.getTaskId(), error);
        flow.setStatus(Flow.Status.ERROR);
        flow.setStatusText(error);
    }

    // FlowScenePluginHandler.java:232
    /**
     * 单个节点上流量任务停止时的后处理操作
     * @param sceneId
     * @param nodeId
     */
    private void stopTaskNode(String sceneId, String nodeId){
        SceneHandler handler = getSceneHandler(sceneId);
        handler.asyncAckElementChanged(nodeId, true, true);
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
            handler.updateSceneElementWithPlugins(nodeId);
        }
    }
}
