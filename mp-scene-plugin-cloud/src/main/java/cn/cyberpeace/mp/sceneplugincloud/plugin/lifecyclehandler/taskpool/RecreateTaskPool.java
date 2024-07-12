 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RecreateTaskPool.java.
 // File size 941.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

/**
 * @ClassName RecreateTaskPool
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TaskPool
 */
public class RecreateTaskPool extends TaskPool {

    private static class CreateTaskPoolInstance {
        public static final RecreateTaskPool INSTANCE = new RecreateTaskPool();
    }

    /**
     * RecreateTaskPool
     */
    private RecreateTaskPool() {
        super(CloudSingleton.cloudAppConfig.getRecreatePoolSize(),
                CloudSingleton.cloudAppConfig.getRecreateHoldSize(), 60L);
    }

    /**
     * getInstance
     *
     * @return
     */
    public static RecreateTaskPool getInstance() {
        return CreateTaskPoolInstance.INSTANCE;
    }
}
