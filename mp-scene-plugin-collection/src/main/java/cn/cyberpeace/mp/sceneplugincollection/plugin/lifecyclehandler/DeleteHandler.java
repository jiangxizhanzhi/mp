package cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.taskpool.DeleteTaskPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class DeleteHandler extends HandlerCommon {

    public static class SceneProcess {
        public static final EventProcess DELETE_FLOW_COLLECTION = new EventProcess("delete_flow_collection", "scenepluginflow.event.delete_flow_collection");
    }

    public static class ElementProcess {
    }

    public DeleteHandler(CollectionScenePluginHandler pluginHandler) {
        super(pluginHandler, DeleteTaskPool.getInstance(), SceneModel.Event.DELETE);
    }

    public void delete() {
        prepareEventProcess(SceneModel.Event.DELETE, SceneProcess.class, ElementProcess.class);

        var scene = getPluginHandler().getScene();
        var flowCollectorId = scene.getFlowCollector();
        if (!StringUtils.isEmpty(flowCollectorId)) {
            getFlowCollector().deleteCollector(flowCollectorId);
        }
    }

}
