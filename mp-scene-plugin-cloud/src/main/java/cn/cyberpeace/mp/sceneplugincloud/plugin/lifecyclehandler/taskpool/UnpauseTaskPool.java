package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

public class UnpauseTaskPool extends TaskPool {
    private static class UnpauseTaskPoolInstance {
        public static final UnpauseTaskPool INSTANCE = new UnpauseTaskPool();
    }

    private UnpauseTaskPool() {
        super(CloudSingleton.cloudAppConfig.getUnpausePoolSize(),
                CloudSingleton.cloudAppConfig.getUnpauseHoldSize(), 60L);
    }

    public static UnpauseTaskPool getInstance() {
        return UnpauseTaskPoolInstance.INSTANCE;
    }
}