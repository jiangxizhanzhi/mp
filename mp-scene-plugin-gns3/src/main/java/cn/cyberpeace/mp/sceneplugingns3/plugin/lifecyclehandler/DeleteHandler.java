package cn.cyberpeace.mp.sceneplugingns3.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugingns3.plugin.Gns3ScenePluginHandler;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.Project;
import cn.cyberpeace.mp.sceneplugingns3.plugin.lifecyclehandler.taskpool.InitiateTaskPool;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeleteHandler extends HandlerCommon {
    private Project project = new Project();

    public static class SceneProcess {
        public static final EventProcess DELETE_GNS3NODES = new EventProcess("delete_gns3nodes", "sceneplugingns3.event.delete_gns3nodes");
    }

    public static class ElementProcess {
        public static final EventProcess DELETE_GNS3NODES = new EventProcess("delete_router", "sceneplugingns3.event.delete_router");
    }


    public DeleteHandler(Gns3ScenePluginHandler pluginHandler) {
        super(pluginHandler, InitiateTaskPool.getInstance(), SceneModel.Event.DELETE);
    }

    public void delete() {
        prepareEventProcess(SceneModel.Event.DELETE, SceneProcess.class, ElementProcess.class);

        
        runSceneProgress(SceneProcess.DELETE_GNS3NODES, () -> {
            String projectId = getPluginHandler().getScene().getResourceId();
            project.stop(projectId);
            project.delete(projectId);
            project.deleteBridge(getPluginHandler().getSourceSceneId().substring(0, 5));
        });
    }

}
