package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

public class RestartTaskPool extends TaskPool {
    private static class RestartTaskPoolInstance {
        public static final RestartTaskPool INSTANCE = new RestartTaskPool();
    }

    private RestartTaskPool() {
        super(CloudSingleton.cloudAppConfig.getRestartPoolSize(),
                CloudSingleton.cloudAppConfig.getRestartHoldSize(), 60L);
    }

    public static RestartTaskPool getInstance() {
        return RestartTaskPoolInstance.INSTANCE;
    }
}