package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

public class CreateNetworkTaskPool extends TaskPool {

    private static class CreateTaskPoolInstance {
        public static final CreateNetworkTaskPool INSTANCE = new CreateNetworkTaskPool();
    }

    private CreateNetworkTaskPool() {
        super(CloudSingleton.cloudAppConfig.getCreateNetworkPoolSize(),
                CloudSingleton.cloudAppConfig.getCreateNetworkHoldSize(), 60L);
    }

    public static CreateNetworkTaskPool getInstance() {
        return CreateTaskPoolInstance.INSTANCE;
    }
}
