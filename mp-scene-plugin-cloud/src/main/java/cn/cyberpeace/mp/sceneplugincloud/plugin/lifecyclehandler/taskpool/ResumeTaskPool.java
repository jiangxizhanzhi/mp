 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ResumeTaskPool.java.
 // File size 919.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

/**
 * @ClassName ResumeTaskPool
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TaskPool
 */
public class ResumeTaskPool extends TaskPool {
    private static class ResumeTaskPoolInstance {
        public static final ResumeTaskPool INSTANCE = new ResumeTaskPool();
    }

    /**
     * ResumeTaskPool
     */
    private ResumeTaskPool() {
        super(CloudSingleton.cloudAppConfig.getResumePoolSize(),
                CloudSingleton.cloudAppConfig.getResumeHoldSize(), 60L);
    }

    /**
     * getInstance
     *
     * @return
     */
    public static ResumeTaskPool getInstance() {
        return ResumeTaskPoolInstance.INSTANCE;
    }
}