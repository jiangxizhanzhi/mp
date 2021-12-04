package cn.cyberpeace.mp.scenepluginflow.plugin.flow;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.flow.enums.TaskActionEnum;
import cn.cyberpeace.mp.flow.model.FlowScriptParamModel;
import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import cn.cyberpeace.mp.flow.request.AddFlowTaskRequest;
import cn.cyberpeace.mp.flow.service.FlowActuatorService;
import cn.cyberpeace.mp.flow.service.impl.AsyncPlatFlowTaskService;
import cn.cyberpeace.mp.flow.service.impl.FlowTaskServiceImpl;
import cn.cyberpeace.mp.scenepluginflow.bo.base.Flow;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

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

    public String createExecutor(String name) {
        return flowService1.addExecutorSync(name);
    }

    public void deleteExecutor(String executorId) {
        flowService2.delExecutor(executorId);
    }

    public String createFlow(String executorId, String targetNetId, String flowId, String fromIps, String toIps,
                             Integer delay, Integer count, Integer interval, Flow flow) {
        var addFlowTaskRequest = new AddFlowTaskRequest();
        addFlowTaskRequest.setActuatorId(executorId);
        addFlowTaskRequest.setNetworkId(targetNetId);
        addFlowTaskRequest.setFlowId(flowId);
        addFlowTaskRequest.setOriginIps(fromIps);
        addFlowTaskRequest.setTargetIps(toIps);
        addFlowTaskRequest.setTitle(TextUtils.uuid());
        if (CollectionUtils.isNotEmpty(flow.getScriptParamModels())) {
            addFlowTaskRequest.setScriptParamModels(flow.getScriptParamModels().stream()
                    .map(param -> {
                        FlowScriptParamModel model = new FlowScriptParamModel();
                        model.setFlowId(flowId);
                        model.setParamDesc(param.getParamDesc());
                        model.setParamKey(param.getParamKey());
                        model.setParamValue(param.getParamValue());
                        model.setParamType(param.getParamType());
                        return model;
                    }).collect(Collectors.toList())
            );
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

    public void startFlow(String taskId, Runnable stopCallback) {
        var result = flowService3.actuator(taskId, TaskActionEnum.START.toValue(), stopCallback);
        if (!result.isSuccess()) {
            throw new MpRuntimeException(result.getServiceCode());
        }
    }

    public void stopFlow(String taskId) {
        var result = flowService3.actuator(taskId, TaskActionEnum.STOP.toValue(), null);
        if (!result.isSuccess()) {
            throw new MpRuntimeException(result.getServiceCode());
        }
    }

    public void deleteFlow(String taskId) {
        var result = flowService3.deleteFlowTask(taskId);
        if (!result.isSuccess()) {
            throw new MpRuntimeException(result.getServiceCode());
        }
    }

    private synchronized void initDefaultInstance() {
        if (DEFAULT_INSTANCE == null) {
            DEFAULT_INSTANCE = new FlowTaskModel();
            DEFAULT_INSTANCE.initDefault();
        }
    }

}
