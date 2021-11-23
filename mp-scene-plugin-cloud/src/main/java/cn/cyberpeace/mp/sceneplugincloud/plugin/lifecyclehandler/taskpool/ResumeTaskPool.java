package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

public class ResumeTaskPool extends TaskPool {
    private static class ResumeTaskPoolInstance {
        public static final ResumeTaskPool INSTANCE = new ResumeTaskPool();
    }

    private ResumeTaskPool() {
        super(CloudSingleton.cloudAppConfig.getResumePoolSize(),
                CloudSingleton.cloudAppConfig.getResumeHoldSize(), 60L);
    }

    public static ResumeTaskPool getInstance() {
        return ResumeTaskPoolInstance.INSTANCE;
    }
}