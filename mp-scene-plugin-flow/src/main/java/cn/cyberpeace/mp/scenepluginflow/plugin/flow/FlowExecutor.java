package cn.cyberpeace.mp.scenepluginflow.plugin.flow;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.flow.enums.TaskActionEnum;
import cn.cyberpeace.mp.flow.model.FlowNetworkInfo;
import cn.cyberpeace.mp.flow.model.FlowScriptParamModel;
import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import cn.cyberpeace.mp.flow.request.AddFlowTaskRequest;
import cn.cyberpeace.mp.flow.request.BatchActuatorTaskRequest;
import cn.cyberpeace.mp.flow.response.BatchActuatorCallbackResponse;
import cn.cyberpeace.mp.flow.service.FlowActuatorService;
import cn.cyberpeace.mp.flow.service.impl.AsyncPlatFlowTaskService;
import cn.cyberpeace.mp.flow.service.impl.FlowTaskServiceImpl;
import cn.cyberpeace.mp.scenepluginflow.bo.base.Flow;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Data
@Component
public class FlowExecutor {
    private static FlowTaskModel DEFAULT_INSTANCE;

    private AsyncPlatFlowTaskService flowService1;

    private FlowActuatorService flowService2;

    private FlowTaskServiceImpl flowService3;

    @Autowired
    public FlowExecutor(AsyncPlatFlowTaskService flowService1, FlowActuatorService flowService2,
                        FlowTaskServiceImpl flowService3) {
        setFlowService1(flowService1);
        setFlowService2(flowService2);
        setFlowService3(flowService3);
    }

    public String createExecutor(String name, List<FlowNetworkInfo> networkInfos) {
        return flowService1.addExecutorSync(name, networkInfos);
    }

    public void deleteExecutor(String executorId) {
        flowService2.delExecutor(executorId);
    }

    public String createFlow(String executorId, String targetNetId, String nodeId, String flowId, String fromIps, String toIps,
                             Integer delay, Integer count, Integer interval, Flow flow, String instanceIp) {
        var addFlowTaskRequest = new AddFlowTaskRequest();
        addFlowTaskRequest.setActuatorId(executorId);
        addFlowTaskRequest.setNetworkId(targetNetId);
        addFlowTaskRequest.setFlowId(flowId);
        addFlowTaskRequest.setOriginIps(fromIps);
        addFlowTaskRequest.setTargetIps(toIps);
        addFlowTaskRequest.setTitle(TextUtils.uuid());
        addFlowTaskRequest.setScriptParamModels(new ArrayList<>());
        addFlowTaskRequest.setNodeId(nodeId);
        boolean ipParamLoaded = false;
        if (CollectionUtils.isNotEmpty(flow.getScriptParamModels())) {
            for (Flow.FlowScriptParam param : flow.getScriptParamModels()) {
                FlowScriptParamModel model = new FlowScriptParamModel();
                model.setFlowId(flowId);
                model.setParamDesc(param.getParamDesc());
                model.setParamKey(param.getParamKey());
                model.setParamType(param.getParamType());
                if (StringUtils.equalsIgnoreCase("ip", param.getParamKey())) {
                    ipParamLoaded = true;
                    model.setParamValue(instanceIp);
                } else {
                    model.setParamValue(param.getParamValue());
                }
                addFlowTaskRequest.getScriptParamModels().add(model);
            }
        }
        if (!ipParamLoaded) {
            FlowScriptParamModel model = new FlowScriptParamModel();
            model.setFlowId(flowId);
            model.setParamKey("ip");
            model.setParamValue(instanceIp);
            addFlowTaskRequest.getScriptParamModels().add(model);
        }
        if (DEFAULT_INSTANCE == null) {
            this.initDefaultInstance();
        }
        addFlowTaskRequest.setFlowPlatform(DEFAULT_INSTANCE.getFlowPlatform());
        addFlowTaskRequest.setDelay(delay == null ? DEFAULT_INSTANCE.getDelay() : delay);
        addFlowTaskRequest.setCount(count == null ? DEFAULT_INSTANCE.getCount() : delay);
        addFlowTaskRequest.setIntervalTime(interval == null ? DEFAULT_INSTANCE.getIntervalTime() : interval);
        var result = flowService3.addFlowTask(addFlowTaskRequest);
        if (!result.isSuccess()) {
            throw new MpRuntimeException(result.getServiceCode());
        }
        return result.getData().getId();
    }

    public List<FlowTaskModel> createFlows(List<AddFlowTaskRequest> addFlowTaskRequests) {
        return flowService3.batchSavePlatFlowTask(addFlowTaskRequests);
    }

    public AddFlowTaskRequest buildFlowTaskRequest(String executorId, String targetNetId, String flowId, String fromIps,
                                                   String toIps, Integer delay, Integer count, Integer interval,
                                                   Flow flow, String instanceIp, String sourceIp, String nodeId) {
        var addFlowTaskRequest = new AddFlowTaskRequest();
        addFlowTaskRequest.setActuatorId(executorId);
        addFlowTaskRequest.setNetworkId(targetNetId);
        addFlowTaskRequest.setFlowId(flowId);
        addFlowTaskRequest.setOriginIps(fromIps);
        addFlowTaskRequest.setTargetIps(toIps);
        addFlowTaskRequest.setTitle(TextUtils.uuid());
        addFlowTaskRequest.setScriptParamModels(new ArrayList<>());
        addFlowTaskRequest.setOriginIps(sourceIp);
        addFlowTaskRequest.setNodeId(nodeId);
        boolean ipParamLoaded = false;
        if (CollectionUtils.isNotEmpty(flow.getScriptParamModels())) {
            for (Flow.FlowScriptParam param : flow.getScriptParamModels()) {
                FlowScriptParamModel model = new FlowScriptParamModel();
                model.setFlowId(flowId);
                model.setParamDesc(param.getParamDesc());
                model.setParamKey(param.getParamKey());
                model.setParamType(param.getParamType());
                if (StringUtils.equalsIgnoreCase("ip", param.getParamKey())) {
                    ipParamLoaded = true;
                    model.setParamValue(instanceIp);
                } else {
                    model.setParamValue(param.getParamValue());
                }
                addFlowTaskRequest.getScriptParamModels().add(model);
            }
        }
        if (!ipParamLoaded) {
            FlowScriptParamModel model = new FlowScriptParamModel();
            model.setFlowId(flowId);
            model.setParamKey("ip");
            model.setParamValue(instanceIp);
            addFlowTaskRequest.getScriptParamModels().add(model);
        }
        if (DEFAULT_INSTANCE == null) {
            this.initDefaultInstance();
        }
        addFlowTaskRequest.setFlowPlatform(DEFAULT_INSTANCE.getFlowPlatform());
        addFlowTaskRequest.setDelay(delay == null ? DEFAULT_INSTANCE.getDelay() : delay);
        addFlowTaskRequest.setCount(count == null ? DEFAULT_INSTANCE.getCount() : delay);
        addFlowTaskRequest.setIntervalTime(interval == null ? DEFAULT_INSTANCE.getIntervalTime() : interval);
        return addFlowTaskRequest;
    }

    public void startFlow(String taskId, Runnable stopCallback, Runnable errorCallback) {
        var result = flowService3.actuator(taskId, TaskActionEnum.START.toValue(), stopCallback, errorCallback);
        if (!result.isSuccess()) {
            throw new MpRuntimeException(result.getCode());
        }
    }

    public void stopFlow(String taskId) {
        var result = flowService3.actuator(taskId, TaskActionEnum.STOP.toValue(), null, null);
        if (!result.isSuccess()) {
            throw new MpRuntimeException(result.getCode());
        }
    }

    public void startFlows(List<String> taskIds, Consumer<BatchActuatorCallbackResponse> callbackConsumer, int runType) {
        BatchActuatorTaskRequest request = new BatchActuatorTaskRequest();
        request.setTaskIds(taskIds);
        request.setRunType(runType);
        request.setTaskAction(TaskActionEnum.START.toValue());
        request.setCallbackConsumer(callbackConsumer);
        var result = flowService3.batchActuator(request);
        if (!result.isSuccess()) {
            throw new MpRuntimeException(result.getCode());
        }
    }

    public void stopFlows(List<String> taskIds) {
        BatchActuatorTaskRequest request = new BatchActuatorTaskRequest();
        request.setTaskAction(TaskActionEnum.STOP.toValue());
        request.setTaskIds(taskIds);
        var result = flowService3.batchActuator(request);
        if (!result.isSuccess()) {
            throw new MpRuntimeException(result.getCode());
        }
    }

    public void deleteFlow(String taskId) {
        var result = flowService3.deleteFlowTask(taskId);
        if (!result.isSuccess()) {
            throw new MpRuntimeException(result.getCode());
        }
    }

    private synchronized void initDefaultInstance() {
        if (DEFAULT_INSTANCE == null) {
            DEFAULT_INSTANCE = new FlowTaskModel();
            DEFAULT_INSTANCE.initDefault();
        }
    }

}
