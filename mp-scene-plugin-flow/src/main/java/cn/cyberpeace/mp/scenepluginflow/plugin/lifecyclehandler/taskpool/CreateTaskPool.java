 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CreateTaskPool.java.
 // File size 782.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scenepluginflow.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scenepluginflow.constant.FlowSingleton;

public class CreateTaskPool extends TaskPool {

    private static class CreateTaskPoolInstance {
        public static final CreateTaskPool INSTANCE = new CreateTaskPool();
    }

    /**
     * CreateTaskPool
     */
    private CreateTaskPool() {
        super(FlowSingleton.flowAppConfig.getCreatePoolSize(),
                FlowSingleton.flowAppConfig.getCreateHoldSize(), 60L);
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
