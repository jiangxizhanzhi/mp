 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ElementTaskPool.java.
 // File size 777.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.service.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;

/**
 * @ClassName ElementTaskPool
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TaskPool
 */
public class ElementTaskPool extends TaskPool {

    private static class ElementTaskPoolInstance {
        public static final ElementTaskPool INSTANCE = new ElementTaskPool();
    }

    private ElementTaskPool() {
        super(SceneSingleton.sceneAppConfig.getElementPoolSize(), SceneSingleton.sceneAppConfig.getElementHoldSize(), 60L);
    }

    public static ElementTaskPool getInstance() {
        return ElementTaskPoolInstance.INSTANCE;
    }

}
