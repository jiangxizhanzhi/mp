package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.RecreateTaskPool;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ElementDeleteHandler extends HandlerCommon {

    private DeleteHandler deleteHandler;

    public ElementDeleteHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, RecreateTaskPool.getInstance(), event);
        this.deleteHandler = new DeleteHandler(pluginHandler);
    }


    public void deleteElement(CloudPluginSceneElementBO element) {
        this.deleteHandler.deleteElementProcess(element);
    }

}
