package cn.cyberpeace.mp.scenepluginconfig.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scene.core.plugin.common.PluginSceneEventHandlerCommon;
import cn.cyberpeace.mp.scenepluginconfig.plugin.ConfigScenePluginHandler;
import cn.cyberpeace.mp.scenepluginconfig.plugin.common.SceneHandlerUtils;
import lombok.Getter;

public class HandlerCommon extends PluginSceneEventHandlerCommon<ConfigScenePluginHandler> {

    @Getter
    private SceneHandlerUtils handlerUtils;

    public HandlerCommon(ConfigScenePluginHandler pluginHandler, TaskPool taskPool, int event) {
        super(pluginHandler, taskPool, event);
        handlerUtils = pluginHandler.getHandlerUtils();
    }

}
