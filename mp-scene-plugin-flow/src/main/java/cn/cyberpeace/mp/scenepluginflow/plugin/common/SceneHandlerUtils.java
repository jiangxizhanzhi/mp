package cn.cyberpeace.mp.scenepluginflow.plugin.common;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneElementBO;
import cn.cyberpeace.mp.scenepluginflow.bo.base.Flow;
import cn.cyberpeace.mp.scenepluginflow.plugin.FlowScenePluginHandler;
import cn.cyberpeace.mp.scenepluginflow.plugin.flow.FlowExecutor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class SceneHandlerUtils {

    private FlowScenePluginHandler pluginHandler;

    private FlowExecutor flowExecutor;

    public SceneHandlerUtils(FlowScenePluginHandler pluginHandler) {
        this.pluginHandler = pluginHandler;
        flowExecutor = BeanUtils.getBean(FlowExecutor.class);
    }

    public String createExecutor() {
        return flowExecutor.createExecutor();
    }

    public void deleteExecutor(String executorId) {
        flowExecutor.deleteExecutor(executorId);
    }

    public boolean startFlow(FlowPluginSceneElementBO flowElement, Flow flow) {
        var taskId = flow.getTaskId();
        if (!StringUtils.isEmpty(taskId)) {
            flowExecutor.startFlow(taskId);
            return true;
        }
        var flowId = flow.getId();
        if (StringUtils.isEmpty(flowId)) {
            return false;
        }

        var executorId = pluginHandler.getScene().getExecutor();
        if (StringUtils.isEmpty(executorId)) {
            return false;
        }

        CloudScenePluginHandler cloudPluginHandler = pluginHandler.getSceneHandler()
                .getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        if (cloudPluginHandler == null) {
            return false;
        }

        var srcElement = flowElement.getSrcElement();
        var srcPorts = srcElement.getPorts();
        if (srcPorts == null) {
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

        if (StringUtils.isEmpty(networkId) || StringUtils.isEmpty(ip)) {
            return false;
        }

        taskId = flowExecutor.createFlow(executorId, networkId, flowId, StringUtils.EMPTY, ip, null, null, null);
        flow.setTaskId(taskId);
        flow.setStatus(Flow.Status.ON);
        return true;
    }

    public boolean stopFlow(Flow flow) {
        var taskId = flow.getTaskId();
        if (StringUtils.isEmpty(taskId)) {
            return false;
        }
        flowExecutor.stopFlow(taskId);
        flow.setStatus(Flow.Status.OFF);
        return true;
    }

    public void deleteFlow(Flow flow) {
        var taskId = flow.getTaskId();
        if (!StringUtils.isEmpty(taskId)) {
            flowExecutor.deleteFlow(taskId);
            flow.setStatus(Flow.Status.OFF);
        }
    }
}
