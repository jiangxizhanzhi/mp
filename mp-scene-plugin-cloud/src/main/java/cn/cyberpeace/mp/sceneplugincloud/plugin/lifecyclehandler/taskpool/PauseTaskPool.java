 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name PauseTaskPool.java.
 // File size 908.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

/**
 * @ClassName PauseTaskPool
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TaskPool
 */
public class PauseTaskPool extends TaskPool {
    private static class PauseTaskPoolInstance {
        public static final PauseTaskPool INSTANCE = new PauseTaskPool();
    }

    /**
     * PauseTaskPool
     */
    private PauseTaskPool() {
        super(CloudSingleton.cloudAppConfig.getPausePoolSize(),
                CloudSingleton.cloudAppConfig.getPauseHoldSize(), 60L);
    }

    /**
     * getInstance
     *
     * @return
     */
    public static PauseTaskPool getInstance() {
        return PauseTaskPoolInstance.INSTANCE;
    }
}