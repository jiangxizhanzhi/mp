package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

public class CreateTaskPool extends CloudTaskPool {

    private static class CreateTaskPoolInstance {
        public static final CreateTaskPool INSTANCE = new CreateTaskPool();
    }

    private CreateTaskPool() {
        setTaskPool(new TaskPool(CloudSingleton.cloudAppConfig.getCreatePoolSize(),
                CloudSingleton.cloudAppConfig.getCreateHoldSize(), 60L));
    }

    public static CreateTaskPool getInstance() {
        return CreateTaskPoolInstance.INSTANCE;
    }
}
