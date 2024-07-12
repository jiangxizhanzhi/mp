 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name InitiateTaskPool.java.
 // File size 945.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

/**
 * @ClassName InitiateTaskPool
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TaskPool
 */
public class InitiateTaskPool extends TaskPool {

    private static class InitiateTaskPoolInstance {
        public static final InitiateTaskPool INSTANCE = new InitiateTaskPool();
    }

    /**
     * InitiateTaskPool
     */
    private InitiateTaskPool() {
        super(CloudSingleton.cloudAppConfig.getInitiatePoolSize(),
                CloudSingleton.cloudAppConfig.getInitiateHoldSize(), 60L);
    }

    /**
     * getInstance
     *
     * @return
     */
    public static InitiateTaskPool getInstance() {
        return InitiateTaskPoolInstance.INSTANCE;
    }
}
