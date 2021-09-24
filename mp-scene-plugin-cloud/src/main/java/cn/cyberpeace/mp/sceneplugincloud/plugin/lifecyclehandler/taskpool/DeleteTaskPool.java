package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

public class DeleteTaskPool extends CloudTaskPool {

    private static class DeleteTaskPoolInstance {
        public static final DeleteTaskPool INSTANCE = new DeleteTaskPool();
    }

    private DeleteTaskPool() {
        setTaskPool(new TaskPool(CloudSingleton.cloudAppConfig.getDeletePoolSize(),
                CloudSingleton.cloudAppConfig.getDeleteHoldSize(), 60L));
    }

    public static DeleteTaskPool getInstance() {
        return DeleteTaskPoolInstance.INSTANCE;
    }

}
