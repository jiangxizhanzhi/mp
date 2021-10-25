package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

public class RecreateTaskPool extends TaskPool {

    private static class CreateTaskPoolInstance {
        public static final RecreateTaskPool INSTANCE = new RecreateTaskPool();
    }

    private RecreateTaskPool() {
        super(CloudSingleton.cloudAppConfig.getCreatePoolSize(),
                CloudSingleton.cloudAppConfig.getRecreatePoolSize(), 60L);
    }

    public static RecreateTaskPool getInstance() {
        return CreateTaskPoolInstance.INSTANCE;
    }
}
