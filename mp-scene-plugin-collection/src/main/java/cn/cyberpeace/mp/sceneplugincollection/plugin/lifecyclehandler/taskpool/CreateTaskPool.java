package cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSingleton;

/**
 * @ClassName CreateTaskPool
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TaskPool
 */
public class CreateTaskPool extends TaskPool {

    private static class CreateTaskPoolInstance {
        public static final CreateTaskPool INSTANCE = new CreateTaskPool();
    }

    /**
     * CreateTaskPool
     */
    private CreateTaskPool() {
        super(CollectionSingleton.collectionAppConfig.getCreatePoolSize(),
                CollectionSingleton.collectionAppConfig.getCreateHoldSize(), 60L);
    }

    /**
     * getInstance
     *
     * @return
     */
    public static CreateTaskPool getInstance() {
        return CreateTaskPoolInstance.INSTANCE;
    }
}
