package cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSingleton;

public class InitiateTaskPool extends TaskPool {

    private static class CreateTaskPoolInstance {
        public static final InitiateTaskPool INSTANCE = new InitiateTaskPool();
    }

    private InitiateTaskPool() {
        super(CollectionSingleton.collectionAppConfig.getInitiatePoolSize(),
                CollectionSingleton.collectionAppConfig.getInitiateHoldSize(), 60L);
    }

    public static InitiateTaskPool getInstance() {
        return CreateTaskPoolInstance.INSTANCE;
    }
}
