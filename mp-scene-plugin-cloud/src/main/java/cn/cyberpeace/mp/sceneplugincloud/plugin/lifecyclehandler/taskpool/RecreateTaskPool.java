package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

/**
 * @ClassName RecreateTaskPool
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TaskPool
 */
public class RecreateTaskPool extends TaskPool {

    private static class CreateTaskPoolInstance {
        public static final RecreateTaskPool INSTANCE = new RecreateTaskPool();
    }

    /**
     * RecreateTaskPool
     */
    private RecreateTaskPool() {
        super(CloudSingleton.cloudAppConfig.getRecreatePoolSize(),
                CloudSingleton.cloudAppConfig.getRecreateHoldSize(), 60L);
    }

    /**
     * getInstance
     *
     * @return
     */
    public static RecreateTaskPool getInstance() {
        return CreateTaskPoolInstance.INSTANCE;
    }
}
