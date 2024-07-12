 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UnpauseTaskPool.java.
 // File size 930.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

/**
 * @ClassName UnpauseTaskPool
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TaskPool
 */
public class UnpauseTaskPool extends TaskPool {
    private static class UnpauseTaskPoolInstance {
        public static final UnpauseTaskPool INSTANCE = new UnpauseTaskPool();
    }

    /**
     * UnpauseTaskPool
     */
    private UnpauseTaskPool() {
        super(CloudSingleton.cloudAppConfig.getUnpausePoolSize(),
                CloudSingleton.cloudAppConfig.getUnpauseHoldSize(), 60L);
    }

    /**
     * getInstance
     *
     * @return
     */
    public static UnpauseTaskPool getInstance() {
        return UnpauseTaskPoolInstance.INSTANCE;
    }
}