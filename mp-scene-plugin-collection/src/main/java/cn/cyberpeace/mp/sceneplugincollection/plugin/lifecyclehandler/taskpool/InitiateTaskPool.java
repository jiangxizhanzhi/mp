package cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSingleton;

/**
 * @ClassName InitiateTaskPool
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TaskPool
 */
public class InitiateTaskPool extends TaskPool {

    private static class CreateTaskPoolInstance {
        public static final InitiateTaskPool INSTANCE = new InitiateTaskPool();
    }

    /**
     * InitiateTaskPool
     */
    private InitiateTaskPool() {
        super(CollectionSingleton.collectionAppConfig.getInitiatePoolSize(),
                CollectionSingleton.collectionAppConfig.getInitiateHoldSize(), 60L);
    }

    /**
     * getInstance
     *
     * @return
     */
    public static InitiateTaskPool getInstance() {
        return CreateTaskPoolInstance.INSTANCE;
    }
}
