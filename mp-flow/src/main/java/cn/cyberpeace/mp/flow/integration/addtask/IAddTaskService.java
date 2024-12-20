package cn.cyberpeace.mp.flow.integration.addtask;

import cn.cyberpeace.mp.flow.bo.AddFlowTaskFlowInfo;

import java.util.List;

public interface IAddTaskService {
    void addTasks(List<AddFlowTaskFlowInfo> tasks, String actuatorId, String networkId, String targetIp);
}
