 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ElementDeleteHandler.java.
 // File size 1149.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.RecreateTaskPool;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName ElementDeleteHandler
 * @Description
 * @Author maojun
 * @Date 2021/10/25
 * @Version 1.0.0
 * @see HandlerCommon
 */
@Slf4j
public class ElementDeleteHandler extends HandlerCommon {

    private DeleteHandler deleteHandler;

    /**
     * ElementDeleteHandler
     *
     * @param pluginHandler
     * @param event
     */
    public ElementDeleteHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, RecreateTaskPool.getInstance(), event);
        this.deleteHandler = new DeleteHandler(pluginHandler);
    }


    /**
     * deleteElement
     *
     * @param element
     */
    public void deleteElement(CloudPluginSceneElementBO element) {
        this.deleteHandler.deleteElementProcess(element);
    }

}
