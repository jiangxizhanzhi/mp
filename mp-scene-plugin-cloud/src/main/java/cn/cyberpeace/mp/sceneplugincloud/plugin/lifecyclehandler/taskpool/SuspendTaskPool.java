package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

public class SuspendTaskPool extends TaskPool {
    private static class SuspendTaskPoolInstance {
        public static final SuspendTaskPool INSTANCE = new SuspendTaskPool();
    }

    private SuspendTaskPool() {
        super(CloudSingleton.cloudAppConfig.getSuspendPoolSize(),
                CloudSingleton.cloudAppConfig.getSuspendHoldSize(), 60L);
    }

    public static SuspendTaskPool getInstance() {
        return SuspendTaskPoolInstance.INSTANCE;
    }
}