package cn.cyberpeace.mp.scenepluginconfig.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scenepluginconfig.constant.ConfigSingleton;

public class DeleteTaskPool extends TaskPool {

    private static class DeleteTaskPoolInstance {
        public static final DeleteTaskPool INSTANCE = new DeleteTaskPool();
    }

    private DeleteTaskPool() {
        super(ConfigSingleton.configAppConfig.getDeletePoolSize(),
                ConfigSingleton.configAppConfig.getDeleteHoldSize(), 60L, "config-delete-task-pool");
    }

    public static DeleteTaskPool getInstance() {
        return DeleteTaskPoolInstance.INSTANCE;
    }

}
