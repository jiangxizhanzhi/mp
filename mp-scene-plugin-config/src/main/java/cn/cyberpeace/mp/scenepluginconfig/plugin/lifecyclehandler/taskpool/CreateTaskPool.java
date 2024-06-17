package cn.cyberpeace.mp.scenepluginconfig.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scenepluginconfig.constant.ConfigSingleton;

public class CreateTaskPool extends TaskPool {

    private static class CreateTaskPoolInstance {
        public static final CreateTaskPool INSTANCE = new CreateTaskPool();
    }

    private CreateTaskPool() {
        super(ConfigSingleton.configAppConfig.getCreatePoolSize(),
                ConfigSingleton.configAppConfig.getCreateHoldSize(), 60L, "config-create-task-pool");
    }

    public static CreateTaskPool getInstance() {
        return CreateTaskPoolInstance.INSTANCE;
    }
}
