 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name StopTaskPool.java.
 // File size 910.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

/**
 * @ClassName StopTaskPool
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TaskPool
 */
public class StopTaskPool extends TaskPool {

    private static class StopTaskPoolInstance {
        public static final StopTaskPool INSTANCE = new StopTaskPool();
    }

    /**
     * StopTaskPool
     */
    private StopTaskPool() {
        super(CloudSingleton.cloudAppConfig.getStopPoolSize(),
            CloudSingleton.cloudAppConfig.getStopHoldSize(), 60L);
    }

    /**
     * getInstance
     *
     * @return
     */
    public static StopTaskPool getInstance() {
        return StopTaskPool.StopTaskPoolInstance.INSTANCE;
    }
}
