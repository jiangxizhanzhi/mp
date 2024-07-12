 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DeleteTaskPool.java.
 // File size 784.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scenepluginflow.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scenepluginflow.constant.FlowSingleton;

public class DeleteTaskPool extends TaskPool {

    private static class DeleteTaskPoolInstance {
        public static final DeleteTaskPool INSTANCE = new DeleteTaskPool();
    }

    /**
     * DeleteTaskPool
     */
    private DeleteTaskPool() {
        super(FlowSingleton.flowAppConfig.getDeletePoolSize(),
                FlowSingleton.flowAppConfig.getDeleteHoldSize(), 60L);
    }

    /**
     * getInstance
     *
     * @return
     */
    public static DeleteTaskPool getInstance() {
        return DeleteTaskPoolInstance.INSTANCE;
    }

}
