package cn.cyberpeace.mp.scenepluginflow.plugin.lifecyclehandler;

import cn.cyberpeace.mp.flow.bo.AddFlowTaskFlowInfo;
import cn.cyberpeace.mp.flow.bo.AddFlowTaskTaskInfo;
import cn.cyberpeace.mp.flow.bo.FlowTaskAttackConf;
import cn.cyberpeace.mp.flow.model.FlowNetworkInfo;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.ConnectedNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.util.ElementTypeUtils;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneBO;
import cn.cyberpeace.mp.scenepluginflow.bo.base.Flow;
import cn.cyberpeace.mp.scenepluginflow.plugin.FlowScenePluginHandler;
import cn.cyberpeace.mp.scenepluginflow.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class CreateHandler extends HandlerCommon {


    public static class SceneProcess {
        public static final EventProcess CREATE_EXECUTOR = new EventProcess("create_executor", "scenepluginflow.event.create_executor");
        public static final EventProcess START_FLOW = new EventProcess("start_flow", "scenepluginflow.event.start_flow");
    }

    public static class ElementProcess {
        public static final EventProcess START_FLOW = new EventProcess("start_flow", "scenepluginflow.event.start_flow");
    }


    public CreateHandler(FlowScenePluginHandler pluginHandler) {
        super(pluginHandler, CreateTaskPool.getInstance(), SceneModel.Event.CREATE);
    }

    public void create() {
        prepareEventProcess(SceneModel.Event.CREATE, SceneProcess.class, ElementProcess.class);

        
        runSceneProgress(SceneProcess.CREATE_EXECUTOR, () -> {
            FlowPluginSceneBO scene = getPluginHandler().getScene();
            List<FlowNetworkInfo> networkInfos = new ArrayList<>();
            var flowElements = new ArrayList<>(getPluginHandler().getElementMap().values());
            log.info("flowElements is:{}", flowElements);
            if (CollectionUtils.isNotEmpty(flowElements)) {
                CloudScenePluginHandler cloudPluginHandler = getPluginHandler().getSceneHandler().getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
                if (cloudPluginHandler != null) {
                    Set<String> existIp = new HashSet<>();
                    List<ConnectedNetworkBO> networkBOS = new ArrayList<>();
                    for (var cpElement : cloudPluginHandler.getElementMap().values()) {
                        if (CollectionUtils.isNotEmpty(cpElement.getDeriveNetworks())) {
                            networkBOS.addAll(cpElement.getDeriveNetworks());
                        }
                    }
                    flowElements.forEach(element -> {
                        var cloudElement = cloudPluginHandler.getElement(element.getSrcElement().getId());
                        if (cloudElement == null || cloudElement.getVirtualType() == null) {
                            return;
                        }
                        if ((!cloudElement.getVirtualType().equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)
                                && !cloudElement.getVirtualType().equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE))
                            || ElementTypeUtils.isCoreRouter(cloudElement.getImage())) {
                            return;
                        }
                        if (CollectionUtils.isNotEmpty(element.getSrcElement().getPorts())) {
                            element.getSrcElement().getPorts().forEach(port -> {
                                var network = cloudPluginHandler.getHandlerUtils().getNetworkByPort(port);
                                var ip = port.getIp();

                                if (StringUtils.isNotEmpty(ip) && !existIp.contains(ip) && network != null) {
                                    var flowNetworkInfo = new FlowNetworkInfo();
                                    flowNetworkInfo.setIp(ip);
                                    flowNetworkInfo.setNetworkId(network.getResourceId());
                                    flowNetworkInfo.setNodeName("MP_SCENE_" + port.getElement().getEid());
                                    networkInfos.add(flowNetworkInfo);
                                    existIp.add(ip);
                                } else if (StringUtils.isNotEmpty(ip) && !existIp.contains(ip) && network == null) {
                                    CloudPluginSceneElementNetworkBO net = null;
                                    for (var cpNet : networkBOS) {
                                        List<SceneElementPortBO> list = new ArrayList<>();
                                        if (CollectionUtils.isNotEmpty(cpNet.getFromPorts())) {
                                            list.addAll(cpNet.getFromPorts());
                                        }
                                        if (CollectionUtils.isNotEmpty(cpNet.getToPorts())) {
                                            list.addAll(cpNet.getToPorts());
                                        }
                                        for (var connectPort : list) {
                                            if (StringUtils.equals(port.getElement().getId(), connectPort.getElement().getId())) {
                                                net = cpNet.getNetwork();
                                            }
                                        }
                                    }

                                    if (net != null) {
                                        var flowNetworkInfo = new FlowNetworkInfo();
                                        flowNetworkInfo.setIp(ip);
                                        flowNetworkInfo.setNetworkId(net.getResourceId());
                                        flowNetworkInfo.setNodeName("MP_SCENE_" + port.getElement().getEid());
                                        networkInfos.add(flowNetworkInfo);
                                        existIp.add(ip);
                                    }
                                } else if (StringUtils.isEmpty(ip) && CollectionUtils.isNotEmpty(networkBOS)) {
                                    CloudPluginSceneElementNetworkBO net = null;
                                    for (var cpNet : networkBOS) {
                                        List<SceneElementPortBO> list = new ArrayList<>();
                                        if (CollectionUtils.isNotEmpty(cpNet.getFromPorts())) {
                                            list.addAll(cpNet.getFromPorts());
                                        }
                                        if (CollectionUtils.isNotEmpty(cpNet.getToPorts())) {
                                            list.addAll(cpNet.getToPorts());
                                        }
                                        for (var connectPort : list) {
                                            if (StringUtils.equals(port.getElement().getId(), connectPort.getElement().getId())) {
                                                net = cpNet.getNetwork();
                                            }
                                        }
                                    }

                                    if (net != null) {
                                        var flowNetworkInfo = new FlowNetworkInfo();
                                        flowNetworkInfo.setNetworkId(net.getResourceId());
                                        flowNetworkInfo.setNodeName("MP_SCENE_" + port.getElement().getEid());
                                        networkInfos.add(flowNetworkInfo);
                                        existIp.add(ip);
                                    }
                                }
                            });
                        }
                    });
                }
            }

            var executorId = getHandlerUtils().startExecutor(getPluginHandler().getSourceSceneId());
            log.info("executorId is:{}", executorId);
            scene.setExecutor(executorId);
            getPluginHandler().saveScene();

            
            var sceneId = getPluginHandler().getSrcScene().getEid();
            List<AddFlowTaskTaskInfo> details = new ArrayList<>();
            getPluginHandler().getElementMap().forEach((nodeId, flowElement) -> {
                List<Flow> flows = flowElement.getFlows();
                if (CollectionUtils.isNotEmpty(flows)) {
                    String nodeName = getPluginHandler().getSceneHandler().getElement(nodeId).getName();
                    String nodeIp = "";
                    String networkId = "";
                    if (CollectionUtils.isNotEmpty(getPluginHandler().getSceneHandler().getElement(nodeId).getPorts())) {
                        CloudScenePluginHandler cloudPluginHandler = getPluginHandler().getSceneHandler().getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
                        for (var p : getPluginHandler().getSceneHandler().getElement(nodeId).getPorts()) {
                            nodeIp = p.getIp();
                            if (StringUtils.isNotEmpty(nodeIp)) {
                                CloudPluginSceneElementNetworkBO cloudPluginSceneElementNetworkBO = cloudPluginHandler.getHandlerUtils().getNetworkByPort(p);
                                if (cloudPluginSceneElementNetworkBO != null) {
                                    networkId = cloudPluginSceneElementNetworkBO.getResourceId();
                                    break;
                                }
                            }
                        }
                    }
                    if (StringUtils.isNotEmpty(networkId)) {
                        AddFlowTaskTaskInfo addFlowTaskTaskInfo = new AddFlowTaskTaskInfo();
                        addFlowTaskTaskInfo.setNodeId(nodeId);
                        addFlowTaskTaskInfo.setNodeName(nodeName);
                        addFlowTaskTaskInfo.setNodeIp(nodeIp);
                        addFlowTaskTaskInfo.setNetworkId(networkId);
                        List<AddFlowTaskFlowInfo> flowInfos = new ArrayList<>();
                        for (Flow flow : flows) {
                            AddFlowTaskFlowInfo addFlowTaskFlowInfo = new AddFlowTaskFlowInfo();
                            addFlowTaskFlowInfo.setFlowId(flow.getId());
                            if (CollectionUtils.isNotEmpty(flow.getScriptParamModels())) {
                                List<FlowTaskAttackConf> attackConfs = new ArrayList<>();
                                for (Flow.FlowScriptParam flowScriptParam : flow.getScriptParamModels()) {
                                    FlowTaskAttackConf flowTaskAttackConf = new FlowTaskAttackConf();
                                    flowTaskAttackConf.setConfValue(flowScriptParam.getParamValue());
                                    flowTaskAttackConf.setConfType(flowScriptParam.getParamType());
                                    flowTaskAttackConf.setConfKey(flowScriptParam.getParamKey());
                                    flowTaskAttackConf.setConfDesc(flowScriptParam.getParamDesc());
                                    attackConfs.add(flowTaskAttackConf);
                                }
                                addFlowTaskFlowInfo.setAttackConfs(attackConfs);
                            }
                            flowInfos.add(addFlowTaskFlowInfo);
                        }
                        addFlowTaskTaskInfo.setFlowInfos(flowInfos);
                        details.add(addFlowTaskTaskInfo);
                    }
                }
            });
            if (CollectionUtils.isNotEmpty(details)) {
                getHandlerUtils().addFlowTask(details, sceneId, executorId);
            }
        });



        













    }

}
