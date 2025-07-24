package cn.cyberpeace.mp.scenepluginflow.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.flow.bo.AddFlowTaskFlowInfo;
import cn.cyberpeace.mp.flow.bo.AddFlowTaskTaskInfo;
import cn.cyberpeace.mp.flow.bo.FlowTaskAttackConf;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneBO;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneElementBO;
import cn.cyberpeace.mp.scenepluginflow.bo.base.Flow;
import cn.cyberpeace.mp.scenepluginflow.constant.FlowSceneStage;
import cn.cyberpeace.mp.scenepluginflow.plugin.FlowScenePluginHandler;
import cn.cyberpeace.mp.scenepluginflow.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

        getPluginHandler().getSceneHandler().getSceneStageRecorder().startStage(FlowSceneStage.CREATE_FLOW_SIMULATE);
        // 创建 trex 虚拟机
        runSceneProgress(SceneProcess.CREATE_EXECUTOR, () -> {
            var executors = getExecutor();
            for (var executor : executors) {
                List<String> nodeIds = new ArrayList<>(executor.getNodeIds());
                List<FlowPluginSceneElementBO> flowElements = new ArrayList<>();
                for (var nodeId : nodeIds) {
                    FlowPluginSceneElementBO flowElement = getPluginHandler().getElement(nodeId);
                    if (flowElement != null) {
                        flowElements.add(flowElement);
                    }
                }
                if (flowElements.isEmpty()) {
                    continue;
                }
                // 给当前分组下的节点创建一个流量执行器
                log.info("create executor for : {}", JsonCommon.stringify(executor.getNodeIds()));
                String executorId = getHandlerUtils().startExecutor(getPluginHandler().getSourceSceneId());
                executor.setExecutorId(executorId);
                log.info("executorId is:{}", executorId);

                //初始化流量任务
                String sceneId = getPluginHandler().getSrcScene().getEid();
                List<AddFlowTaskTaskInfo> details = new ArrayList<>();
                for (FlowPluginSceneElementBO f : flowElements) {
                    String nodeId = f.getSrcElement().getId();
                    var flowElement = f;
                    List<Flow> flows = flowElement.getFlows();
                    if (CollectionUtils.isNotEmpty(flows)) {
                        String nodeName = getPluginHandler().getSceneHandler().getElement(nodeId).getName();
                        String nodeIp = "";
                        String networkId = "";
                        if (CollectionUtils.isNotEmpty(getPluginHandler().getSceneHandler().getElement(nodeId)
                                                               .getPorts())) {
                            CloudScenePluginHandler cloudPluginHandler = getPluginHandler().getSceneHandler()
                                    .getPluginHandler(PluginConstant.PLUGIN_KEY);
                            for (SceneElementPortBO p : getPluginHandler().getSceneHandler().getElement(nodeId)
                                    .getPorts()) {
                                nodeIp = p.getIp();
                                if (StringUtils.isNotEmpty(nodeIp)) {
                                    CloudPluginSceneElementNetworkBO cloudPluginSceneElementNetworkBO = cloudPluginHandler.getHandlerUtils()
                                            .getNetworkByPort(p);
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
                }
                if (CollectionUtils.isNotEmpty(details)) {
                    getHandlerUtils().addFlowTask(details, sceneId, executorId);
                }
            }
            FlowPluginSceneBO scene = getPluginHandler().getScene();
            scene.setExecutor(executors);
            getPluginHandler().saveScene();
        });
        getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(FlowSceneStage.CREATE_FLOW_SIMULATE);


        // 启动流量任务 创建暂不启动
//        List<Runnable> startFlowTasks = new ArrayList<>();
//        getPluginHandler().getElementMap().forEach((nodeId, flowElement) -> {
//            List<Flow> flows = flowElement.getFlows();
//            if (flows == null) {
//                return;
//            }
//            flows.forEach(flow -> startFlowTasks.add(() -> {
//                runElementProgress(nodeId, ElementProcess.START_FLOW, () -> getHandlerUtils().startFlow(flowElement, flow));
//            }));
//        });
//        if (!startFlowTasks.isEmpty()) {
//            runSceneProgress(SceneProcess.START_FLOW, () -> runTasks(startFlowTasks));
//        }
    }

    @Getter
    @Setter
    private static class NetGroupElement {
        private String networkId;
        private String cidr;
        private List<String> nodeIds;
    }

    public List<FlowPluginSceneBO.Executor> getExecutor() {
        CloudScenePluginHandler cloudScenePluginHandler = getPluginHandler().getSceneHandler()
                .getPluginHandler(PluginConstant.PLUGIN_KEY);

        List<NetGroupElement> netGroupElements = new ArrayList<>();
        for (var srcElement : getPluginHandler().getSceneHandler().getElementMap().values()) {
            var ports = srcElement.getPorts();
            String ip = "";
            String networkId = "";
            for (var p : ports) {
                if (StringUtils.isNotEmpty(p.getIp())) {
                    ip = p.getIp();
                    var cloudPort = cloudScenePluginHandler.getPort(p.getId());
                    networkId = cloudPort.getAuxiliaryNetworkId();
                    break;
                }
            }
            if (StringUtils.isEmpty(ip)) {
                continue;
            }
            // 取ip前三个值作为cidr
            String cidr = ip.substring(0, ip.lastIndexOf("."));
            NetGroupElement netGroupElement = null;
            for (var ne : netGroupElements) {
                if (StringUtils.equals(networkId, ne.getNetworkId())) {
                    // 同一个网络下的节点 同一组
                    netGroupElement = ne;
                    break;
                }
                if (!StringUtils.equals(cidr, ne.getCidr())) {
                    // 不同网络 且 网段cidr不同的 同一组
                    netGroupElement = ne;
                    break;
                }
            }
            if (netGroupElement == null) {
                // 不同网段 且 网段cidr相同的 不同组
                netGroupElement = new NetGroupElement();
                netGroupElement.setCidr(cidr);
                netGroupElement.setNetworkId(networkId);
                netGroupElement.setNodeIds(new ArrayList<>());
                netGroupElements.add(netGroupElement);
            }
            netGroupElement.getNodeIds().add(srcElement.getId());
        }

        var result = new ArrayList<FlowPluginSceneBO.Executor>();
        for (var group : netGroupElements) {
            FlowPluginSceneBO.Executor executor = new FlowPluginSceneBO.Executor();
            executor.setExecutorId("");
            executor.setNodeIds(new HashSet<>(group.getNodeIds()));
            result.add(executor);
        }
        return result;
    }
}
