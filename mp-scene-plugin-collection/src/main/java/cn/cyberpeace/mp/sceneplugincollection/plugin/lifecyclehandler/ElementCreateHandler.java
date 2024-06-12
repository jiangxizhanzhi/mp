package cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler;

import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ElementCreateHandler extends HandlerCommon {

    public static class SceneProcess {
    }

    public static class ElementProcess {
    }

    public ElementCreateHandler(CollectionScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, CreateTaskPool.getInstance(), event);
    }

    public void createElement(CollectionPluginSceneElementBO element) {
        return;
    }

}
