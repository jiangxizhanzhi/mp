 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name StartTaskPool.java.
 // File size 922.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

/**
 * @ClassName StartTaskPool
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TaskPool
 */
public class StartTaskPool extends TaskPool {

    private static class StartTaskPoolInstance {
        public static final StartTaskPool INSTANCE = new StartTaskPool();
    }

    /**
     * StartTaskPool
     */
    private StartTaskPool() {
        super(CloudSingleton.cloudAppConfig.getStartPoolSize(),
            CloudSingleton.cloudAppConfig.getStartHoldSize(), 60L);
    }

    /**
     * getInstance
     *
     * @return
     */
    public static StartTaskPool getInstance() {
        return StartTaskPool.StartTaskPoolInstance.INSTANCE;
    }
}
