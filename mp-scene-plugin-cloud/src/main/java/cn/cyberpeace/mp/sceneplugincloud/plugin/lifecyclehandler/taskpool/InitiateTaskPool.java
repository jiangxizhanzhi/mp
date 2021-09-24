package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

public class InitiateTaskPool extends CloudTaskPool {

    private static class InitiateTaskPoolInstance {
        public static final InitiateTaskPool INSTANCE = new InitiateTaskPool();
    }

    private InitiateTaskPool() {
        setTaskPool(new TaskPool(CloudSingleton.cloudAppConfig.getInitiatePoolSize(),
                CloudSingleton.cloudAppConfig.getInitiateHoldSize(), 60L));
    }

    public static InitiateTaskPool getInstance() {
        return InitiateTaskPoolInstance.INSTANCE;
    }
}
