package cn.cyberpeace.mp.scenepluginflow.plugin.common;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.flow.model.FlowNetworkInfo;
import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import cn.cyberpeace.mp.flow.request.AddFlowTaskRequest;
import cn.cyberpeace.mp.flow.response.BatchActuatorCallbackResponse;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneElementBO;
import cn.cyberpeace.mp.scenepluginflow.plugin.params.FlowWithSourceIp;
import cn.cyberpeace.mp.scenepluginflow.bo.base.Flow;
import cn.cyberpeace.mp.scenepluginflow.plugin.FlowScenePluginHandler;
import cn.cyberpeace.mp.scenepluginflow.plugin.flow.FlowExecutor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Data
@Slf4j
public class SceneHandlerUtils {

    private FlowScenePluginHandler pluginHandler;

    private FlowExecutor flowExecutor;

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
        log.info("flow error,flowId is {},taskId is {}", flow.getId(), flow.getTaskId());
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
        try {
            log.info("start flow for taskId:[{}]", taskId);
            flowExecutor.startFlow(taskId, () -> {
                successOffFLow(flow);
                if (stopCallback != null) {
                    stopCallback.run();
                }
            }, () -> {
                errorFLow(flow, "Flow execute error");
                if (Objects.nonNull(stopCallback)) {
                    stopCallback.run();
                }
            });
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
        Map<String, Flow> flowMap = new HashMap<>();
        flows.forEach(flow -> {
            flowMap.put(flow.getId(), flow);
        });
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
            flowExecutor.startFlows(taskIds, callbackConsumer, runType);
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
    }

    public String createExecutor(String sceneEid, List<FlowNetworkInfo> networkInfos) {
        return flowExecutor.createExecutor(sceneEid, networkInfos);
    }

    public void deleteExecutor(String executorId) {
        flowExecutor.deleteExecutor(executorId);
    }

    public boolean startFlow(FlowPluginSceneElementBO flowElement, Flow flow, Runnable stopCallback,
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
        final String flowNetworkId = networkId;
        final String flowIp = ip;
        String finalIp = ip;
        Callable<Boolean> start = () -> {
            String flowTaskId;
            try {
                flowTaskId = flowExecutor.createFlow(executorId, flowNetworkId, flowId, StringUtils.EMPTY, flowIp, null, null, null, flow, finalIp);
                log.info("flowExecutor createFlow success:[{}]", flowTaskId);
            } catch (Exception e) {
                errorFLow(flow, e.getMessage());
                return false;
            }

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
                              boolean async, Runnable asyncCallback, int runType) {
        var needCreateTaskFlows = flowWithSourceIps.stream()
                .filter(f -> f.getFlow().getTaskId() == null)
                .collect(Collectors.toList());
        var flows = flowWithSourceIps.stream()
                .map(f -> f.getFlow())
                .collect(Collectors.toList());
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

        var emptyIdFlows = needCreateTaskFlows.stream()
                .filter(flow -> flow.getFlow().getId() == null)
                .collect(Collectors.toList());
        if (!emptyIdFlows.isEmpty()) {
            emptyIdFlows.forEach(flow -> {
                errorFLow(flow.getFlow(), "Empty flow id");
            });
        }

        var newFlows = needCreateTaskFlows.stream()
                .filter(flow -> flow.getFlow().getId() != null)
                .collect(Collectors.toList());
        if (newFlows.isEmpty()) {
            return false;
        }
        newFlows.forEach(flow -> {
            preparingFLow(flow.getFlow());
        });
        final String flowNetworkId = networkId;
        final String flowIp = ip;
        String finalIp = ip;
        String nodeId = flowElement.getSrcElement().getId();
        Callable<Boolean> start = () -> {
            List<FlowTaskModel> flowTaskModels;
            List<AddFlowTaskRequest> requests = new ArrayList<>();
            newFlows.forEach(flow -> {
                AddFlowTaskRequest request = flowExecutor.buildFlowTaskRequest(executorId, flowNetworkId, flow.getFlow().getId(),
                        StringUtils.EMPTY, flowIp, null, null, null, flow.getFlow(), finalIp,
                        flow.getSourceIp(), nodeId);
                requests.add(request);
            });
            try {
                flowTaskModels = flowExecutor.createFlows(requests);
                log.info("flowExecutor createFlows success:[{}]", flowTaskModels);
            } catch (Exception e) {
                newFlows.forEach(flow -> {
                    errorFLow(flow.getFlow(), e.getMessage());
                });
                return false;
            }

            Map<String, FlowTaskModel> flowId2TaskMap = new HashMap<>();
            flowTaskModels.forEach(flowTaskModel -> {
                flowId2TaskMap.put(flowTaskModel.getFlowId(), flowTaskModel);
            });
            var executorFlows = newFlows.stream().map(f -> f.getFlow()).collect(Collectors.toList());
            executorFlows.forEach(flow -> {
                flow.setTaskId(flowId2TaskMap.get(flow.getId()).getId());
            });
            return startFlows(executorFlows, stopCallback, runType);
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
