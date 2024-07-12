 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CreateTaskPool.java.
 // File size 958.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSingleton;

/**
 * @ClassName CreateTaskPool
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TaskPool
 */
public class CreateTaskPool extends TaskPool {

    private static class CreateTaskPoolInstance {
        public static final CreateTaskPool INSTANCE = new CreateTaskPool();
    }

    /**
     * CreateTaskPool
     */
    private CreateTaskPool() {
        super(CollectionSingleton.collectionAppConfig.getCreatePoolSize(),
                CollectionSingleton.collectionAppConfig.getCreateHoldSize(), 60L);
    }

    /**
     * getInstance
     *
     * @return
     */
    public static CreateTaskPool getInstance() {
        return CreateTaskPoolInstance.INSTANCE;
    }
}
