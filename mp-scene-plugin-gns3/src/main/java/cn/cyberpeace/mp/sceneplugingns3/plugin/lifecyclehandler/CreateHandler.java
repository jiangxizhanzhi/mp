package cn.cyberpeace.mp.sceneplugingns3.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugingns3.bo.Gns3PluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugingns3.plugin.Gns3ScenePluginHandler;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.Cloud;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.Link;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.Project;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.Router;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.Switch;
import cn.cyberpeace.mp.sceneplugingns3.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class CreateHandler extends HandlerCommon {

    private Project project = new Project();
    private Cloud cloud = new Cloud();
    private Switch swit = new Switch();
    private Router router = new Router();
    private Link link = new Link();

    public static class SceneProcess {
        public static final EventProcess CREATE_GNS3NODES = new EventProcess("create_gns3nodes", "sceneplugingns3.event.create_gns3nodes");
    }

    public static class ElementProcess {
        public static final EventProcess CREATE_ROUTER = new EventProcess("create_router", "sceneplugingns3.event.create_router");
    }


    public CreateHandler(Gns3ScenePluginHandler pluginHandler) {
        super(pluginHandler, CreateTaskPool.getInstance(), SceneModel.Event.CREATE);
    }

    public void create() {
        prepareEventProcess(SceneModel.Event.CREATE, SceneProcess.class, ElementProcess.class);
        Set<String> nodeIds = new HashSet<>(getPluginHandler().getElementIds());

        
        CloudScenePluginHandler cloudScenePluginHandler = getPluginHandler().getSceneHandler().getPluginHandler(PluginConstant.PLUGIN_KEY);
        var createGatewayTasks = cloudScenePluginHandler.getGatewayElementMap().values().stream()
                .filter(c -> nodeIds.contains(c.getSrcElement().getId()))
                .map(c -> createRouterProcess(getPluginHandler().getElement(c.getSrcElement().getId())))
                .collect(Collectors.toList());

        if (!createGatewayTasks.isEmpty()) {
            runSceneProgress(SceneProcess.CREATE_GNS3NODES, () -> runTasks(createGatewayTasks));
        }
    }

    private Runnable createRouterProcess(Gns3PluginSceneElementBO e) {
        return () -> runElementProgress(e.getSrcElement().getId(), ElementProcess.CREATE_ROUTER, () -> createRouter(e));
    }

    private void createRouter(Gns3PluginSceneElementBO e) {
        CloudScenePluginHandler cloudScenePluginHandler = getPluginHandler().getSceneHandler().getPluginHandler(PluginConstant.PLUGIN_KEY);
        var networks = cloudScenePluginHandler.getElement(e.getSrcElement().getId()).getDeriveNetworks();
        
        
        
    }

}
