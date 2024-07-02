package cn.cyberpeace.mp.scenepluginflow.plugin.lifecyclehandler;

import cn.cyberpeace.mp.flow.bo.AddFlowTaskFlowInfo;
import cn.cyberpeace.mp.flow.bo.AddFlowTaskTaskInfo;
import cn.cyberpeace.mp.flow.bo.FlowTaskAttackConf;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneBO;
import cn.cyberpeace.mp.scenepluginflow.bo.base.Flow;
import cn.cyberpeace.mp.scenepluginflow.constant.FlowSceneStage;
import cn.cyberpeace.mp.scenepluginflow.plugin.FlowScenePluginHandler;
import cn.cyberpeace.mp.scenepluginflow.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CreateHandler extends HandlerCommon {


    public static class SceneProcess {
        public static final EventProcess CREATE_EXECUTOR = new EventProcess("create_executor", "scenepluginflow.event.create_executor");
        public static final EventProcess START_FLOW = new EventProcess("start_flow", "scenepluginflow.event.start_flow");
    }

    public static class ElementProcess {
        public static final EventProcess START_FLOW = new EventProcess("start_flow", "scenepluginflow.event.start_flow");
    }


    /**
     * CreateHandler
     *
     * @param pluginHandler
     */
    public CreateHandler(FlowScenePluginHandler pluginHandler) {
        super(pluginHandler, CreateTaskPool.getInstance(), SceneModel.Event.CREATE);
    }

    /**
     * create
     */
    public void create() {
        prepareEventProcess(SceneModel.Event.CREATE, SceneProcess.class, ElementProcess.class);

        getPluginHandler().getSceneHandler().getSceneStageRecorder().startStage(FlowSceneStage.CREATE_FLOW_SIMULATE);
        // 创建 trex 虚拟机
        runSceneProgress(SceneProcess.CREATE_EXECUTOR, () -> {
            FlowPluginSceneBO scene = getPluginHandler().getScene();

            // 3. 起trex
            String executorId = getHandlerUtils().startExecutor(getPluginHandler().getSourceSceneId());
            log.info("executorId is:{}", executorId);
            scene.setExecutor(executorId);
            getPluginHandler().saveScene();

            //初始化流量任务
            String sceneId = getPluginHandler().getSrcScene().getEid();
            List<AddFlowTaskTaskInfo> details = new ArrayList<>();
            getPluginHandler().getElementMap().forEach((nodeId, flowElement) -> {
                List<Flow> flows = flowElement.getFlows();
                if (CollectionUtils.isNotEmpty(flows)) {
                    String nodeName = getPluginHandler().getSceneHandler().getElement(nodeId).getName();
                    String nodeIp = "";
                    String networkId = "";
                    if (CollectionUtils.isNotEmpty(getPluginHandler().getSceneHandler().getElement(nodeId).getPorts())) {
                        CloudScenePluginHandler cloudPluginHandler = getPluginHandler().getSceneHandler().getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
                        for (SceneElementPortBO p : getPluginHandler().getSceneHandler().getElement(nodeId).getPorts()) {
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
        getPluginHandler().getSceneHandler().getSceneStageRecorder().endStage(FlowSceneStage.CREATE_FLOW_SIMULATE);
    }

}
