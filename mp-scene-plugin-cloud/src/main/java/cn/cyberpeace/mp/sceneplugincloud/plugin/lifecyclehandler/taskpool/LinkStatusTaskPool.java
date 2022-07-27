package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

public class LinkStatusTaskPool extends TaskPool {

    private static class CreateTaskPoolInstance {
        public static final LinkStatusTaskPool INSTANCE = new LinkStatusTaskPool();
    }

    private LinkStatusTaskPool() {
        super(CloudSingleton.cloudAppConfig.getLinkStatusPoolSize(),
                CloudSingleton.cloudAppConfig.getLinkStatusHoldSize(), 60L);
    }

    public static LinkStatusTaskPool getInstance() {
        return CreateTaskPoolInstance.INSTANCE;
    }
}
