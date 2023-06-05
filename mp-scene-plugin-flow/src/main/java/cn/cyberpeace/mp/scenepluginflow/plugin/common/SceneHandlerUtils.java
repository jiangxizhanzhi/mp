package cn.cyberpeace.mp.scenepluginflow.plugin.common;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.flow.bo.AddFlowTaskTaskInfo;
import cn.cyberpeace.mp.flow.model.FlowNetworkInfo;
import cn.cyberpeace.mp.flow.model.FlowTaskTaskModel;
import cn.cyberpeace.mp.flow.response.BatchActuatorCallbackResponse;
import cn.cyberpeace.mp.flow.service.FlowTaskTaskService;
import cn.cyberpeace.mp.flow.util.FlowCacheUtil;
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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Data
@Slf4j
public class SceneHandlerUtils {

    private FlowScenePluginHandler pluginHandler;

    private FlowExecutor flowExecutor;

    private FlowTaskTaskService flowTaskTaskService;

    private void successOnFLow(Flow flow) {
        log.info("flow success on,flowId is {},taskId is {}", flow.getId(), flow.getTaskId());
        flow.setStatus(Flow.Status.ON);
        flow.setStatusText(null);
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

    private void preparingFLow(Flow flow) {
        log.info("flow preparing ,flowId is {},taskId is {}", flow.getId(), flow.getTaskId());
        flow.setStatus(Flow.Status.PREPARING);
        flow.setStatusText(null);
    }

    private boolean startFlow(Flow flow, Runnable stopCallback) {
        var taskId = flow.getTaskId();

        Consumer<String> formatStopCallback = new Consumer<String>() {
            @Override
            public void accept(String s) {
                errorFLow(flow, s);
                if (Objects.nonNull(stopCallback)) {
                    stopCallback.run();
                }
            }
        };

        try {
            log.info("start flow for taskId:[{}]", taskId);
            flowExecutor.startFlow(taskId, () -> {
                successOffFLow(flow);
                if (stopCallback != null) {
                    stopCallback.run();
                }
            }, formatStopCallback, flow.getSourceIp());
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

    private boolean startFlows(List<Flow> flows, Runnable stopCallback, int runType) {
        var taskIds = flows.stream()
                .map(f -> f.getTaskId())
                .collect(Collectors.toList());
        Map<String, Flow> flowMap = flows.stream().collect(Collectors.toMap(Flow::getId, flow -> flow));
        try {
            log.info("start flows for taskIds:[{}]", taskIds);

            Consumer<BatchActuatorCallbackResponse> callbackConsumer = new Consumer<BatchActuatorCallbackResponse>() {
                @Override
                public void accept(BatchActuatorCallbackResponse batchActuatorCallbackResponse) {
                    batchActuatorCallbackResponse.getErrorTaskModels().forEach(flowTaskModel -> {
                        errorFLow(flowMap.get(flowTaskModel.getFlowId()), "Flow execute error");
                    });
                    batchActuatorCallbackResponse.getSuccessTaskModels().forEach(flowTaskModel -> {
                        successOffFLow(flowMap.get(flowTaskModel.getFlowId()));
                    });
                    if (Objects.nonNull(stopCallback)) {
                        stopCallback.run();
                    }
                }
            };

            String sceneId = pluginHandler.getSourceSceneId();
            FlowCacheUtil.taskConsumerCache.put(FlowCacheUtil.CONSUMER_PREFIX + sceneId, callbackConsumer);
            flowExecutor.startFlows(taskIds, callbackConsumer, runType);
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

    public SceneHandlerUtils(FlowScenePluginHandler pluginHandler) {
        this.pluginHandler = pluginHandler;
        flowExecutor = BeanCommon.getBean(FlowExecutor.class);
        flowTaskTaskService = BeanCommon.getBean(FlowTaskTaskService.class);
    }

    public String createExecutor(String sceneEid, List<FlowNetworkInfo> networkInfos) {
        String name = BeanCommon.getBean(IFlowResourceName.class).generate(sceneEid, null);
        return flowExecutor.createExecutor(name, networkInfos);
    }

    public String startExecutor(String sceneEid) {
        String name = BeanCommon.getBean(IFlowResourceName.class).generate(sceneEid, null);
        return flowExecutor.startExecutor(name);
    }

    public void deleteExecutor(String executorId) {
        flowExecutor.deleteExecutor(executorId);
    }

    public void addFlowTask(List<AddFlowTaskTaskInfo> details, String sceneId, String actuatorId) {
        flowExecutor.addFlowTask(details, sceneId, actuatorId);
    }

    public boolean startFlow(FlowPluginSceneElementBO flowElement, Flow flow,String sourceIp, Runnable stopCallback,
                             boolean async, Runnable asyncCallback) {
        var taskId = flow.getTaskId();
        if (!StringUtils.isEmpty(taskId)) {
            return startFlow(flow, stopCallback);
        } else {
            log.info("taskId is empty");
        }

        var flowId = flow.getId();
        if (StringUtils.isEmpty(flowId)) {
            errorFLow(flow, "Empty flow id");
            return false;
        }

        var executorId = pluginHandler.getScene().getExecutor();
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

        var srcElement = flowElement.getSrcElement();
        var srcPorts = srcElement.getPorts();
        if (srcPorts == null) {
            errorFLow(flow, "Network port not found");
            return false;
        }

        String networkId = null;
        String ip = null;
        for (var srcPort : srcPorts) {
            var cloudPort = cloudPluginHandler.getPort(srcPort.getId());
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
//        final String flowNetworkId = networkId;
//        final String flowIp = ip;
//        String finalIp = ip;
        String fromIps = sourceIp == null ? StringUtils.EMPTY : sourceIp;
        flow.setSourceIp(fromIps);
        List<FlowTaskTaskModel> flowTaskTaskModels = flowTaskTaskService.listByNodeIdAndFlowId(srcElement.getEid(), flowId);

        Callable<Boolean> start = () -> {
            String flowTaskId = "";
            if (CollectionUtils.isNotEmpty(flowTaskTaskModels)) {
                flowTaskId = flowTaskTaskModels.get(0).getId();
            }
//            try {
//                flowTaskId = flowExecutor.createFlow(executorId, flowNetworkId, srcElement.getEid(), flowId, fromIps, flowIp, null, null, null, flow, finalIp);
//                log.info("flowExecutor createFlow success:[{}]", flowTaskId);
//            } catch (Exception e) {
//                errorFLow(flow, e.getMessage());
//                return false;
//            }
            flow.setTaskId(flowTaskId);
            return startFlow(flow, stopCallback);
        };
        if (async) {
            TaskCommon.executeTask(() -> {
                try {
                    start.call();
                } catch (Exception e) {
                    log.info("Start flow error: ", e);
                }
                if (asyncCallback != null) {
                    asyncCallback.run();
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

    public boolean stopFlow(Flow flow) {
        var taskId = flow.getTaskId();
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

    public boolean startFlows(FlowPluginSceneElementBO flowElement, List<FlowWithSourceIp> flowWithSourceIps, Runnable stopCallback,
                              boolean async, Runnable asyncCallback, int runType, String nodeId) {
        // 需要创建流量任务的流量
        var needCreateTaskFlows = flowWithSourceIps.stream()
                .filter(f -> StringUtils.isEmpty(f.getFlow().getTaskId()))
                .collect(Collectors.toList());
        var flows = flowWithSourceIps.stream()
                .map(f -> f.getFlow())
                .collect(Collectors.toList());

        List<FlowTaskTaskModel> flowTaskTaskModels = flowTaskTaskService.listByNodeId(nodeId);
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
            });
        }

        if (needCreateTaskFlows.isEmpty()) {
            return startFlows(flows, stopCallback, runType);
        } else {
            log.info("taskIds has null value");
        }

        var executorId = pluginHandler.getScene().getExecutor();
        if (StringUtils.isEmpty(executorId)) {
            flows.forEach(flow -> {
                errorFLow(flow, "Empty executor id");
            });
            return false;
        }

        CloudScenePluginHandler cloudPluginHandler = pluginHandler.getSceneHandler()
                .getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        if (cloudPluginHandler == null) {
            flows.forEach(flow -> {
                errorFLow(flow, "Cloud plugin not found");
            });
            return false;
        }

        var srcElement = flowElement.getSrcElement();
        var srcPorts = srcElement.getPorts();
        if (srcPorts == null) {
            flows.forEach(flow -> {
                errorFLow(flow, "Network port not found");
            });
            return false;
        }

        String networkId = null;
        String ip = null;
        for (var srcPort : srcPorts) {
            var cloudPort = cloudPluginHandler.getPort(srcPort.getId());
            networkId = cloudPort.getAuxiliaryNetworkId();
            ip = srcPort.getIp();
            if (!StringUtils.isEmpty(networkId) && !StringUtils.isEmpty(ip)) {
                break;
            }
        }

        if (StringUtils.isEmpty(networkId)) {
            flows.forEach(flow -> {
                errorFLow(flow, "Empty network id");
            });
            return false;
        }

        if (StringUtils.isEmpty(ip)) {
            flows.forEach(flow -> {
                errorFLow(flow, "Empty ip");
            });
            return false;
        }

        // 将流量id为空的流量状态置为错误
        var emptyIdFlows = needCreateTaskFlows.stream()
                .filter(flow -> flow.getFlow().getId() == null)
                .collect(Collectors.toList());
        if (!emptyIdFlows.isEmpty()) {
            emptyIdFlows.forEach(flow -> {
                errorFLow(flow.getFlow(), "Empty flow id");
            });
        }

        // 流量id不为空的流量创建流量任务
        var newFlows = needCreateTaskFlows.stream()
                .filter(flow -> flow.getFlow().getId() != null)
                .collect(Collectors.toList());
        // 最终需要执行的流量集合
        var executeFlows = flows.stream()
                .filter(flow -> StringUtils.isNotEmpty(flow.getId()))
                .collect(Collectors.toList());
        if (newFlows.isEmpty()) {
            return false;
        }
        executeFlows.forEach(flow -> {
            preparingFLow(flow);
        });

        Callable<Boolean> start = () -> {
            return startFlows(executeFlows, stopCallback, runType);
        };
        if (async) {
            TaskCommon.executeTask(() -> {
                try {
                    start.call();
                } catch (Exception e) {
                    log.info("Start flows error: ", e);
                }
                if (asyncCallback != null) {
                    asyncCallback.run();
                }
            });
            return true;
        } else {
            try {
                return start.call();
            } catch (Exception e) {
                log.info("Start flows error: ", e);
                return false;
            }
        }
    }

    public boolean stopFlows(List<Flow> flows) {
        List<String> taskIds = flows.stream()
                .filter(flow -> flow.getTaskId() != null)
                .map(flow -> flow.getTaskId())
                .collect(Collectors.toList());
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

    public boolean  deleteFlow(Flow flow) {
        var taskId = flow.getTaskId();
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
