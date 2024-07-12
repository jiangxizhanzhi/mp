 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HandlerCommon.java.
 // File size 871.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scenepluginflow.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scene.core.plugin.common.PluginSceneEventHandlerCommon;
import cn.cyberpeace.mp.scenepluginflow.plugin.FlowScenePluginHandler;
import cn.cyberpeace.mp.scenepluginflow.plugin.common.SceneHandlerUtils;
import lombok.Getter;

public class HandlerCommon extends PluginSceneEventHandlerCommon<FlowScenePluginHandler> {

    @Getter
    private SceneHandlerUtils handlerUtils;

    /**
     * HandlerCommon
     *
     * @param pluginHandler
     * @param taskPool
     * @param event
     */
    public HandlerCommon(FlowScenePluginHandler pluginHandler, TaskPool taskPool, int event) {
        super(pluginHandler, taskPool, event);
        handlerUtils = pluginHandler.getHandlerUtils();
    }

}
