package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

/**
 * @ClassName RestartTaskPool
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TaskPool
 */
public class RestartTaskPool extends TaskPool {
    private static class RestartTaskPoolInstance {
        public static final RestartTaskPool INSTANCE = new RestartTaskPool();
    }

    /**
     * RestartTaskPool
     */
    private RestartTaskPool() {
        super(CloudSingleton.cloudAppConfig.getRestartPoolSize(),
                CloudSingleton.cloudAppConfig.getRestartHoldSize(), 60L);
    }

    /**
     * getInstance
     *
     * @return
     */
    public static RestartTaskPool getInstance() {
        return RestartTaskPoolInstance.INSTANCE;
    }
}