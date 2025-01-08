package cn.cyberpeace.mp.flow.integration.addtask;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.flow.config.FlowConfig;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class AddTaskByLocalFlowFacade {
    @Resource
    @Qualifier(value = "addTaskByRemoteFlowService")
    private IAddTaskService addTaskByRemoteFlowService;

    @Resource
    @Qualifier(value = "addTaskByLocalFlowService")
    private IAddTaskService addTaskByLocalFlowService;

    @Resource
    @Qualifier(value = "addTaskByRemoteFlowService2")
    private IAddTaskService addTaskByRemoteFlowService2;

    public void addTasksToDb(FlowActuatorModel flowActuatorModel) {
        FlowConfig flowConfig = BeanCommon.getBean(FlowConfig.class);
        if ("local".equals(flowConfig.getFlowResMode())) {
            log.info("使用 addTaskByLocalFlowService");
            addTaskByLocalFlowService.addTasksToDb(flowActuatorModel);
        } else if ("remote".equals(flowConfig.getFlowResMode())) {
            log.info("使用 addTaskByRemoteFlowService");
            addTaskByRemoteFlowService.addTasksToDb(flowActuatorModel);
        } else {
            log.info("使用 addTaskByRemoteFlowService2");
            addTaskByRemoteFlowService2.addTasksToDb(flowActuatorModel);
        }
    }

    public void addTasksToActuator(FlowActuatorModel flowActuatorModel) {
        FlowConfig flowConfig = BeanCommon.getBean(FlowConfig.class);
        if ("local".equals(flowConfig.getFlowResMode())) {
            log.info("使用 addTaskByLocalFlowService");
            addTaskByLocalFlowService.addTasksToActuator(flowActuatorModel);
        } else if ("remote".equals(flowConfig.getFlowResMode())) {
            log.info("使用 addTaskByRemoteFlowService");
            addTaskByRemoteFlowService.addTasksToActuator(flowActuatorModel);
        } else {
            log.info("使用 addTaskByRemoteFlowService2");
            addTaskByRemoteFlowService2.addTasksToActuator(flowActuatorModel);
        }
    }
}
