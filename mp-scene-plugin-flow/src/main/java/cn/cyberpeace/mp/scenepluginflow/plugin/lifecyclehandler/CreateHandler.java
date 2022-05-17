package cn.cyberpeace.mp.scenepluginflow.plugin.lifecyclehandler;

import cn.cyberpeace.mp.flow.model.FlowNetworkInfo;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneBO;
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

        // 创建 trex 虚拟机
        runSceneProgress(SceneProcess.CREATE_EXECUTOR, () -> {
            FlowPluginSceneBO scene = getPluginHandler().getScene();
            List<FlowNetworkInfo> networkInfos = new ArrayList<>();
            var flowElements = new ArrayList<>(getPluginHandler().getElementMap().values());
            if (CollectionUtils.isNotEmpty(flowElements)) {
                CloudScenePluginHandler cloudPluginHandler = getPluginHandler().getSceneHandler().getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
                if (cloudPluginHandler != null) {
                    Set<String> existIp = new HashSet<>();
                    flowElements.forEach(element -> {
                        if (CollectionUtils.isNotEmpty(element.getSrcElement().getPorts())) {
                            element.getSrcElement().getPorts().forEach(port -> {
                                var network = cloudPluginHandler.getHandlerUtils().getNetworkByPort(port);
                                var ip = port.getIp();
                                if (StringUtils.isNotEmpty(ip) && !existIp.contains(ip) && network != null) {
                                    var flowNetworkInfo = new FlowNetworkInfo();
                                    flowNetworkInfo.setIp(ip);
                                    flowNetworkInfo.setNetworkId(network.getResourceId());
                                    networkInfos.add(flowNetworkInfo);
                                    existIp.add(ip);
                                }
                            });
                        }
                    });
                }
            }
            var executorId = getHandlerUtils().createExecutor(getPluginHandler().getSourceSceneId(), networkInfos);
            scene.setExecutor(executorId);
            getPluginHandler().saveScene();
        });

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

}
