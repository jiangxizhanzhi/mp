package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

/**
 * @ClassName SuspendTaskPool
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TaskPool
 */
public class SuspendTaskPool extends TaskPool {
    private static class SuspendTaskPoolInstance {
        public static final SuspendTaskPool INSTANCE = new SuspendTaskPool();
    }

    /**
     * SuspendTaskPool
     */
    private SuspendTaskPool() {
        super(CloudSingleton.cloudAppConfig.getSuspendPoolSize(),
                CloudSingleton.cloudAppConfig.getSuspendHoldSize(), 60L);
    }

    /**
     * getInstance
     *
     * @return
     */
    public static SuspendTaskPool getInstance() {
        return SuspendTaskPoolInstance.INSTANCE;
    }
}