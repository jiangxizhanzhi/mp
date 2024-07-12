 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name InitiateHandler.java.
 // File size 944.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scenepluginflow.plugin.lifecyclehandler;

import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.scenepluginflow.plugin.FlowScenePluginHandler;
import cn.cyberpeace.mp.scenepluginflow.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InitiateHandler extends HandlerCommon {

    /**
     * InitiateHandler
     *
     * @param pluginHandler
     */
    public InitiateHandler(FlowScenePluginHandler pluginHandler) {
        super(pluginHandler, CreateTaskPool.getInstance(), SceneModel.Event.INITIATE);
    }

    /**
     * initiate
     */
    public void initiate() {
        CloudScenePluginHandler pluginHandler = getPluginHandler().getSceneHandler().getPluginHandler("cpcloud");
        pluginHandler.getScene().setEnableRate(true);
    }
}
