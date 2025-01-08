package cn.cyberpeace.mp.flow.integration.addtask;

import cn.cyberpeace.mp.flow.model.FlowActuatorModel;

public interface IAddTaskService {
    void addTasksToDb(FlowActuatorModel flowActuatorModel);

    void addTasksToActuator(FlowActuatorModel flowActuatorModel);
}
