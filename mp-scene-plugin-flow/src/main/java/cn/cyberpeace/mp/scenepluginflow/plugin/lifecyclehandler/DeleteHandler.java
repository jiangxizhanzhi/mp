package cn.cyberpeace.mp.scenepluginflow.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scenepluginflow.plugin.FlowScenePluginHandler;
import cn.cyberpeace.mp.scenepluginflow.plugin.lifecyclehandler.taskpool.DeleteTaskPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class DeleteHandler extends HandlerCommon {

    public static class SceneProcess {
        public static final EventProcess DELETE_EXECUTOR = new EventProcess("delete_executor", "scenepluginflow.event.delete_executor");
        public static final EventProcess DELETE_FLOW = new EventProcess("delete_flow", "scenepluginflow.event.delete_flow");
    }

    public static class ElementProcess {
        public static final EventProcess DELETE_FLOW = new EventProcess("delete_flow", "scenepluginflow.event.delete_flow");
    }

    public DeleteHandler(FlowScenePluginHandler pluginHandler) {
        super(pluginHandler, DeleteTaskPool.getInstance(), SceneModel.Event.DELETE);
    }

    public void delete() {
        prepareEventProcess(SceneModel.Event.DELETE, SceneProcess.class, ElementProcess.class);

        // TODO event
        var scene = getPluginHandler().getScene();

        // TODO 并发
        getPluginHandler().getElementMap().forEach((nodeId, flowElement) -> {
            var flows = flowElement.getFlows();
            if (flows == null) {
                return;
            }

            flows.forEach(getHandlerUtils()::deleteFlow);
        });

        var executorId = scene.getExecutor();
        if (StringUtils.isEmpty(executorId)) {
            getHandlerUtils().deleteExecutor(executorId);
        }
    }

}
