package cn.cyberpeace.mp.scenepluginflow.plugin.common;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneElementBO;
import cn.cyberpeace.mp.scenepluginflow.bo.base.Flow;
import cn.cyberpeace.mp.scenepluginflow.plugin.FlowScenePluginHandler;
import cn.cyberpeace.mp.scenepluginflow.plugin.flow.FlowExecutor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Data
@Slf4j
public class SceneHandlerUtils {

    private FlowScenePluginHandler pluginHandler;

    private FlowExecutor flowExecutor;

    private void successOnFLow(Flow flow) {
        flow.setStatus(Flow.Status.ON);
        flow.setStatusText(null);
    }

    private void successOffFLow(Flow flow) {
        flow.setStatus(Flow.Status.OFF);
        flow.setStatusText(null);
    }

    private void errorFLow(Flow flow, String error) {
        flow.setStatus(Flow.Status.ERROR);
        flow.setStatusText(error);
    }

    public SceneHandlerUtils(FlowScenePluginHandler pluginHandler) {
        this.pluginHandler = pluginHandler;
        flowExecutor = BeanCommon.getBean(FlowExecutor.class);
    }

    public String createExecutor(String sceneEid) {
        return flowExecutor.createExecutor(sceneEid);
    }

    public void deleteExecutor(String executorId) {
        flowExecutor.deleteExecutor(executorId);
    }

    public boolean startFlow(FlowPluginSceneElementBO flowElement, Flow flow) {
        var taskId = flow.getTaskId();
        if (!StringUtils.isEmpty(taskId)) {
            try {
                log.info("start flow for taskId:[{}]", taskId);
                flowExecutor.startFlow(taskId, () -> {
                    successOffFLow(flow);
                });
            } catch (Exception e) {
                log.error("start flow for taskId:[{}] error", taskId, e);
                errorFLow(flow, e.getMessage());
                return false;
            }
            successOnFLow(flow);
            return true;
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

        try {
            taskId = flowExecutor.createFlow(executorId, networkId, flowId, StringUtils.EMPTY, ip, null, null, null);
            log.info("flowExecutor createFlow success:[{}]", taskId);
        } catch (Exception e) {
            errorFLow(flow, e.getMessage());
            return false;
        }

        flow.setTaskId(taskId);
        return startFlow(flowElement, flow);
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

    public boolean deleteFlow(Flow flow) {
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
