package cn.cyberpeace.mp.sceneplugingns3.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugingns3.plugin.Gns3ScenePluginHandler;
import cn.cyberpeace.mp.sceneplugingns3.plugin.lifecyclehandler.taskpool.InitiateTaskPool;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InitiateHandler extends HandlerCommon {


    public static class SceneProcess {
        public static final EventProcess INIT_GNS3NODES = new EventProcess("init_gns3nodes", "scenepluginflow.event.create_executor");
    }

    public static class ElementProcess {
        public static final EventProcess INIT_GNS3NODES = new EventProcess("init_gns3nodes", "scenepluginflow.event.start_flow");
    }


    public InitiateHandler(Gns3ScenePluginHandler pluginHandler) {
        super(pluginHandler, InitiateTaskPool.getInstance(), SceneModel.Event.INITIATE);
    }

    public void initiate() {
        prepareEventProcess(SceneModel.Event.INITIATE, SceneProcess.class, ElementProcess.class);

        
        runSceneProgress(SceneProcess.INIT_GNS3NODES, () -> {
            CloudScenePluginHandler cloudPlugin = getPluginHandler().getSceneHandler().getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
            
            getPluginHandler().getElementMap().values().forEach(e -> {
                var cloudElement = cloudPlugin.getElement(e.getSrcElement().getId());
                cloudElement.setExternal(true);
            });
        });
    }

}
