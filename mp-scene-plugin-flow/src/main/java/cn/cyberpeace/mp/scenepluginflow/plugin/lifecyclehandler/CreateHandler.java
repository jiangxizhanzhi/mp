package cn.cyberpeace.mp.scenepluginflow.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scenepluginflow.plugin.FlowScenePluginHandler;
import cn.cyberpeace.mp.scenepluginflow.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import lombok.extern.slf4j.Slf4j;

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

        // TODO event
        var scene = getPluginHandler().getScene();
        var executorId = getHandlerUtils().createExecutor();
        scene.setExecutor(executorId);
        getPluginHandler().saveScene();

        // TODO 并发
        getPluginHandler().getElementMap().forEach((nodeId, flowElement) -> {
            var flows = flowElement.getFlows();
            if (flows == null) {
                return;
            }

            flows.forEach(flow -> getHandlerUtils().startFlow(flowElement, flow));
        });

    }

}
