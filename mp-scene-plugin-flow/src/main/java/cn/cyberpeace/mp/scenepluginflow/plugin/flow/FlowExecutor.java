package cn.cyberpeace.mp.scenepluginflow.plugin.flow;

import cn.cyberpeace.mp.flow.enums.TaskActionEnum;
import cn.cyberpeace.mp.flow.request.AddFlowTaskRequest;
import cn.cyberpeace.mp.flow.service.FlowActuatorService;
import cn.cyberpeace.mp.flow.service.impl.AsyncPlatFlowTaskService;
import cn.cyberpeace.mp.flow.service.impl.FlowTaskServiceImpl;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Data
@Component
public class FlowExecutor {

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

    public String createExecutor() {
        return flowService1.addExecutor();
    }

    public void deleteExecutor(String executorId) {
        flowService2.delExecutor(executorId);
    }

    public String createFlow(String executorId, String targetNetId, String flowId, String fromIps, String toIps,
                            Integer delay, Integer count, Integer interval) {
        var addFlowTaskRequest = new AddFlowTaskRequest();
        addFlowTaskRequest.setActuatorId(executorId);
        addFlowTaskRequest.setNetworkId(targetNetId);
        addFlowTaskRequest.setFlowId(flowId);
        addFlowTaskRequest.setOriginIps(fromIps);
        addFlowTaskRequest.setTargetIps(toIps);
        addFlowTaskRequest.setDelay(delay);
        addFlowTaskRequest.setCount(count);
        addFlowTaskRequest.setIntervalTime(interval);
        var task = flowService3.addFlowTask(addFlowTaskRequest).getSource();
        return task == null ? null : task.getId();
    }

    public void startFlow(String taskId) {
        flowService3.actuator(taskId, TaskActionEnum.START.toValue());
    }

    public void stopFlow(String taskId) {
        flowService3.actuator(taskId, TaskActionEnum.STOP.toValue());
    }

    public void deleteFlow(String taskId) {
        flowService3.deleteFlowTask(taskId);
    }

}
