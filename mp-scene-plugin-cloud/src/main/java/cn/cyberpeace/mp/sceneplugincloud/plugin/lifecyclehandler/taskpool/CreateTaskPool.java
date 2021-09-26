package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

public class CreateTaskPool extends TaskPool {

    private static class CreateTaskPoolInstance {
        public static final CreateTaskPool INSTANCE = new CreateTaskPool();
    }

    private CreateTaskPool() {
        super(CloudSingleton.cloudAppConfig.getCreatePoolSize(),
                CloudSingleton.cloudAppConfig.getCreateHoldSize(), 60L);
    }

    public static CreateTaskPool getInstance() {
        return CreateTaskPoolInstance.INSTANCE;
    }
}
