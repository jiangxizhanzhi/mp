package cn.cyberpeace.mp.sceneplugingns3.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scene.core.plugin.common.PluginSceneEventHandlerCommon;
import cn.cyberpeace.mp.sceneplugingns3.plugin.Gns3ScenePluginHandler;
import cn.cyberpeace.mp.sceneplugingns3.plugin.common.SceneHandlerUtils;
import lombok.Getter;

public class HandlerCommon extends PluginSceneEventHandlerCommon<Gns3ScenePluginHandler> {

    @Getter
    private SceneHandlerUtils handlerUtils;

    public HandlerCommon(Gns3ScenePluginHandler pluginHandler, TaskPool taskPool, int event) {
        super(pluginHandler, taskPool, event);
        this.handlerUtils = pluginHandler.getHandlerUtils();
    }

}
