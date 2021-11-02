package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

public class PauseTaskPool extends TaskPool {
    private static class PauseTaskPoolInstance {
        public static final PauseTaskPool INSTANCE = new PauseTaskPool();
    }

    private PauseTaskPool() {
        super(CloudSingleton.cloudAppConfig.getPausePoolSize(),
                CloudSingleton.cloudAppConfig.getPauseHoldSize(), 60L);
    }

    public static PauseTaskPool getInstance() {
        return PauseTaskPoolInstance.INSTANCE;
    }
}