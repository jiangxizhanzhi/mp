package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.RecreateTaskPool;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class PrepareHandler extends HandlerCommon {


    public PrepareHandler(CloudScenePluginHandler pluginHandler) {
        super(pluginHandler, RecreateTaskPool.getInstance(), SceneElementModel.Event.INITIATE);
    }

    public void prepare(boolean partialStart) {
        if (partialStart) {
            getPluginHandler().getElementMap().values().forEach(e -> {
                if (e.getSrcElement().getType() == BaseSceneElementModel.Type.TERMINAL) {
                    if (e.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.CONTAINER || e.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE) {
                        getPluginHandler().getSceneHandler().addExcludeHandleNode(e.getSrcElement().getId());
                    }
                }
            });
        }
    }
}
