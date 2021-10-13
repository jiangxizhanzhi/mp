package cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InitiateHandler extends HandlerCommon {


    public static class SceneProcess {
        public static final EventProcess INITIATE_PROBES = new EventProcess("initiate_probes", "sceneplugincollection.event.initiate_probes");
    }

    public static class ElementProcess {
    }

    private void addCollectionPort(String portId, CollectionPluginSceneElementPortBO collectionPort) {
        getPluginHandler().getElementIds().add(portId);
        getPluginHandler().getElementPortMap().put(portId, collectionPort);

        var srcPort = getPluginHandler().getSrcPort(portId);
        collectionPort.setSrcPort(srcPort);
    }

    public InitiateHandler(CollectionScenePluginHandler pluginHandler) {
        super(pluginHandler, CreateTaskPool.getInstance(), SceneModel.Event.INITIATE);
    }

    public void initiate() {
        prepareEventProcess(SceneModel.Event.INITIATE, SceneProcess.class, ElementProcess.class);

        var scene = getPluginHandler().getScene();
        // 全局开启流量采集
        if (Boolean.TRUE.equals(scene.getEnableFlowCollect())) {
            getPluginHandler().getSceneHandler().getElementPortMap().forEach((portId, srcPort) -> {
                var collectionPort = getPluginHandler().getPort(portId);
                if (collectionPort == null) {
                    collectionPort = new CollectionPluginSceneElementPortBO();
                    addCollectionPort(portId, collectionPort);
                }
                collectionPort.setEnableFlowCollect(true);
            });
        }
    }

}
