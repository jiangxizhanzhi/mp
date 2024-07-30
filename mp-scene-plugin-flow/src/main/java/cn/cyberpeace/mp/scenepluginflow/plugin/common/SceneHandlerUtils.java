 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneHandlerUtils.java.
 // File size 14292.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scenepluginflow.plugin.common;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.flow.bo.AddFlowTaskTaskInfo;
import cn.cyberpeace.mp.flow.bo.BatchStartTaskBO;
import cn.cyberpeace.mp.flow.bo.TaskSipBO;
import cn.cyberpeace.mp.flow.model.FlowTaskTaskModel;
import cn.cyberpeace.mp.flow.repository.FlowTaskTaskMapper;
import cn.cyberpeace.mp.flow.service.FlowTaskTaskService;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneElementBO;
import cn.cyberpeace.mp.scenepluginflow.bo.base.Flow;
import cn.cyberpeace.mp.scenepluginflow.plugin.FlowScenePluginHandler;
import cn.cyberpeace.mp.scenepluginflow.plugin.flow.FlowExecutor;
import cn.cyberpeace.mp.scenepluginflow.plugin.params.FlowWithSourceIp;
import cn.cyberpeace.mp.scenepluginflow.util.IFlowResourceName;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.Callable;

@Data
@Slf4j
public class SceneHandlerUtils {

    private FlowScenePluginHandler pluginHandler;

    private FlowExecutor flowExecutor;

    private FlowTaskTaskService flowTaskTaskService;

    /**
     * successOnFLow
     *
     * @param flow
     */
    private void successOnFLow(Flow flow) {
        log.info("flow success on,flowId is {},taskId is {}", flow.getId(), flow.getTaskId());
        flow.setStatus(Flow.Status.ON);
        flow.setStatusText(null);
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

    /**
     * preparingFLow
     *
     * @param flow
     */
    private void preparingFLow(Flow flow) {
        log.info("flow preparing ,flowId is {},taskId is {}", flow.getId(), flow.getTaskId());
        flow.setStatus(Flow.Status.PREPARING);
        flow.setStatusText(null);
    }

    /**
     * startFlow
     *
     * @param flow
     * @return boolean
     */
    private boolean startFlow(Flow flow) {
        String taskId = flow.getTaskId();
        try {
            log.info("start flow for taskId:[{}]", taskId);
            String sourceIp = flow.getSourceIp();
            if (StringUtils.isBlank(sourceIp)) {
                sourceIp = "127.0.0.1";
            }
            flowExecutor.startFlow(taskId, sourceIp);
        } catch (Exception e) {
            log.error("start flow for taskId:[{}] error", taskId, e);
            errorFLow(flow, e.getMessage());
            return false;
        }
        // 流量执行次数
        flow.setCount(Objects.nonNull(flow.getCount()) ? (flow.getCount() + 1) : 1);
        successOnFLow(flow);
        return true;
    }

    /**
     * startFlows
     *
     * @param flows
     * @param runType
     * @return boolean
     */
    private boolean startFlows(List<Flow> flows, int runType) {
        BatchStartTaskBO batchStartTaskBO = new BatchStartTaskBO();
        List<TaskSipBO> taskSipBOList = new ArrayList<>();
        flows.forEach(flow -> {
            TaskSipBO taskSipBO = new TaskSipBO();
            taskSipBO.setTaskId(flow.getTaskId());
            taskSipBO.setSourceIp(flow.getSourceIp());
            taskSipBOList.add(taskSipBO);
        });
        batchStartTaskBO.setTaskSipList(taskSipBOList);
        batchStartTaskBO.setRunType(runType);

        List<String> taskIds = new ArrayList<>();
        for (Flow f : flows) {
            String taskId = f.getTaskId();
            taskIds.add(taskId);
        }
        Map<String, Flow> flowMap = new HashMap<>();
        for (Flow flow1 : flows) {
            if (flowMap.put(flow1.getId(), flow1) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }
        try {
            log.info("start flows for taskIds:[{}]", taskIds);
            flowExecutor.startFlows(batchStartTaskBO);
        } catch (MpRuntimeException e) {
            log.error("start flows for taskIds:[{}] error", taskIds, e);
            flows.forEach(flow -> {
                errorFLow(flow, e.getMessage());
            });
            throw e;
        } catch (Exception e) {
            log.error("start flows for taskIds:[{}] error", taskIds, e);
            flows.forEach(flow -> {
                errorFLow(flow, e.getMessage());
            });
            return false;
        }
        // 流量执行次数
        flows.forEach(flow -> {
            flow.setCount(Objects.nonNull(flow.getCount()) ? (flow.getCount() + 1) : 1);
            successOnFLow(flow);
        });
        return true;
    }

    /**
     * SceneHandlerUtils
     *
     * @param pluginHandler
     */
    public SceneHandlerUtils(FlowScenePluginHandler pluginHandler) {
        this.pluginHandler = pluginHandler;
        flowExecutor = BeanCommon.getBean(FlowExecutor.class);
        flowTaskTaskService = BeanCommon.getBean(FlowTaskTaskService.class);
    }

    /**
     * startExecutor
     *
     * @param sceneEid
     * @return
     */
    public String startExecutor(String sceneEid) {
        String name = BeanCommon.getBean(IFlowResourceName.class).generate(sceneEid, null);
        return flowExecutor.startExecutor(name);
    }

    /**
     * deleteExecutor
     *
     * @param executorId
     */
    public void deleteExecutor(String executorId) {
        flowExecutor.deleteExecutor(executorId);

        BeanCommon.getBean(FlowTaskTaskMapper.class)
                .filterDelete(new HumanCondition().filterEqual(FlowTaskTaskModel::getActuatorId, executorId));
    }

    /**
     * addFlowTask
     *
     * @param details
     * @param sceneId
     * @param actuatorId
     */
    public void addFlowTask(List<AddFlowTaskTaskInfo> details, String sceneId, String actuatorId) {
        flowExecutor.addFlowTask(details, sceneId, actuatorId);
    }

    /**
     * deleteByNodeIdAndSceneId
     *
     * @param nodeId
     * @param sceneId
     */
    public void deleteByNodeIdAndSceneId(String nodeId, String sceneId) {
        flowExecutor.deleteByNodeIdAndSceneId(nodeId, sceneId);
    }

    /**
     * startFlow
     *
     * @param flowElement
     * @param flow
     * @param sourceIp
     * @param async
     * @return boolean
     */
    public boolean startFlow(FlowPluginSceneElementBO flowElement, Flow flow,String sourceIp,
                             boolean async) {
        String taskId = flow.getTaskId();
        if (!StringUtils.isEmpty(taskId)) {
            return startFlow(flow);
        } else {
            log.info("taskId is empty");
        }

        String flowId = flow.getId();
        if (StringUtils.isEmpty(flowId)) {
            errorFLow(flow, "Empty flow id");
            return false;
        }

        String executorId = pluginHandler.getScene().getExecutor();
        if (StringUtils.isEmpty(executorId)) {
            errorFLow(flow, "Empty executor id");
            return false;
        }

        CloudScenePluginHandler cloudPluginHandler = pluginHandler.getSceneHandler()
                .getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        if (cloudPluginHandler == null) {
            errorFLow(flow, "Cloud plugin not found");
            return false;
        }

        SceneElementBO srcElement = flowElement.getSrcElement();
        List<SceneElementPortBO> srcPorts = srcElement.getPorts();
        if (srcPorts == null) {
            errorFLow(flow, "Network port not found");
            return false;
        }

        String networkId = null;
        String ip = null;
        for (SceneElementPortBO srcPort : srcPorts) {
            CloudPluginSceneElementPortBO cloudPort = cloudPluginHandler.getPort(srcPort.getId());
            networkId = cloudPort.getAuxiliaryNetworkId();
            ip = srcPort.getIp();
            if (!StringUtils.isEmpty(networkId) && !StringUtils.isEmpty(ip)) {
                break;
            }
        }

        if (StringUtils.isEmpty(networkId)) {
            errorFLow(flow, "Empty network id");
            return false;
        }

        if (StringUtils.isEmpty(ip)) {
            errorFLow(flow, "Empty ip");
            return false;
        }

        preparingFLow(flow);
        String fromIps = sourceIp == null ? StringUtils.EMPTY : sourceIp;
        flow.setSourceIp(fromIps);
        log.info("get flow task info by nodeId: {} and flowId: {}", srcElement.getId(), flowId);
        List<FlowTaskTaskModel> flowTaskTaskModels = flowTaskTaskService.listByNodeIdAndFlowId(srcElement.getId(), flowId);

        Callable<Boolean> start = () -> {
            String flowTaskId = "";
            if (CollectionUtils.isNotEmpty(flowTaskTaskModels)) {
                flowTaskId = flowTaskTaskModels.get(0).getId();
            }
            flow.setTaskId(flowTaskId);
            boolean res = startFlow(flow);
            SceneHandler handler = pluginHandler.getSceneHandler();
            handler.asyncAckElementChanged(flowElement.getSrcElement().getId(), true, true);
            if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
                handler.updateSceneElementWithPlugins(flowElement.getSrcElement().getId());
            }
            return res;
        };
        if (async) {
            TaskCommon.executeTask(() -> {
                try {
                    start.call();
                } catch (Exception e) {
                    log.info("Start flow error: ", e);
                }
            });
            return true;
        } else {
            try {
                return start.call();
            } catch (Exception e) {
                log.info("Start flow error: ", e);
                return false;
            }
        }
    }

    /**
     * stopFlow
     *
     * @param flow
     * @return boolean
     */
    public boolean stopFlow(Flow flow) {
        String taskId = flow.getTaskId();
        if (StringUtils.isEmpty(taskId)) {
            errorFLow(flow, "Empty task id");
            return false;
        }

        try {
            flowExecutor.stopFlow(taskId);
        } catch (Exception e) {
            errorFLow(flow, e.getMessage());
            return false;
        }
        successOffFLow(flow);
        return true;
    }

    /**
     * startFlows
     *
     * @param flowElement
     * @param flowWithSourceIps
     * @param async
     * @param asyncCallback
     * @param runType
     * @param nodeId
     * @return boolean
     */
    public boolean startFlows(FlowPluginSceneElementBO flowElement, List<FlowWithSourceIp> flowWithSourceIps,
                              boolean async, Runnable asyncCallback, int runType, String nodeId) {
        List<Flow> flows = new ArrayList<>();
        for (FlowWithSourceIp f : flowWithSourceIps) {
            Flow fFlow = f.getFlow();
            flows.add(fFlow);
        }

        String sceneId = pluginHandler.getSourceSceneId();
        List<FlowTaskTaskModel> flowTaskTaskModels = flowTaskTaskService.listByNodeIdAndSceneId(nodeId, sceneId);
        if (CollectionUtils.isEmpty(flowTaskTaskModels)) {
            flows.forEach(flow -> {
                errorFLow(flow, "FlowTask not exist");
            });
            return false;
        } else {
            // 设置taskId
            flows.forEach(flow -> {
                flowTaskTaskModels.forEach(flowTaskTaskModel -> {
                    if (flow.getId().equals(flowTaskTaskModel.getFlowId())) {
                        flow.setTaskId(flowTaskTaskModel.getId());
                    }
                });
                preparingFLow(flow);
            });
        }

        return startFlows(flows, runType);
    }

    /**
     * stopFlows
     *
     * @param flows
     * @return boolean
     */
    public boolean stopFlows(List<Flow> flows) {
        List<String> taskIds = new ArrayList<>();
        for (Flow flow1 : flows) {
            if (flow1.getTaskId() != null) {
                String taskId = flow1.getTaskId();
                taskIds.add(taskId);
            }
        }
        if (taskIds.isEmpty()) {
            flows.forEach(flow -> {
                errorFLow(flow, "Empty task id");
            });
            return false;
        }

        try {
            flowExecutor.stopFlows(taskIds);
        } catch (Exception e) {
            flows.forEach(flow -> {
                errorFLow(flow, e.getMessage());
            });
            return false;
        }
        flows.forEach(flow -> {
            successOffFLow(flow);
        });
        return true;
    }

    /**
     * deleteFlow
     *
     * @param flow
     * @return boolean
     */
    public boolean  deleteFlow(Flow flow) {
        String taskId = flow.getTaskId();
        if (!StringUtils.isEmpty(taskId)) {
            try {
                flowExecutor.deleteFlow(taskId);
            } catch (Exception e) {
                errorFLow(flow, e.getMessage());
                return false;
            }
            successOffFLow(flow);
            return true;
        } else {
            return true;
        }
    }
}
