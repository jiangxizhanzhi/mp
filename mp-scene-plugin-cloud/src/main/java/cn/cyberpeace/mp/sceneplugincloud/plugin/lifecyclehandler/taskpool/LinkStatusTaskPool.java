 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LinkStatusTaskPool.java.
 // File size 959.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

/**
 * @ClassName LinkStatusTaskPool
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TaskPool
 */
public class LinkStatusTaskPool extends TaskPool {

    private static class CreateTaskPoolInstance {
        public static final LinkStatusTaskPool INSTANCE = new LinkStatusTaskPool();
    }

    /**
     * LinkStatusTaskPool
     */
    private LinkStatusTaskPool() {
        super(CloudSingleton.cloudAppConfig.getLinkStatusPoolSize(),
                CloudSingleton.cloudAppConfig.getLinkStatusHoldSize(), 60L);
    }

    /**
     * getInstance
     *
     * @return
     */
    public static LinkStatusTaskPool getInstance() {
        return CreateTaskPoolInstance.INSTANCE;
    }
}
