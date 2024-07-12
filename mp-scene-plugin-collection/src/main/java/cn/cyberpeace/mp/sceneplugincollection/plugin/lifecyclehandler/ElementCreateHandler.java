 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ElementCreateHandler.java.
 // File size 1133.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler;

import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName ElementCreateHandler
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see HandlerCommon
 */
@Slf4j
public class ElementCreateHandler extends HandlerCommon {

    public static class SceneProcess {
    }

    public static class ElementProcess {
    }

    /**
     * ElementCreateHandler
     *
     * @param pluginHandler
     * @param event
     */
    public ElementCreateHandler(CollectionScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, CreateTaskPool.getInstance(), event);
    }

    /**
     * createElement
     *
     * @param element
     */
    public void createElement(CollectionPluginSceneElementBO element) {
        return;
    }

}
