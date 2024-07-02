package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

/**
 * @ClassName PauseTaskPool
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TaskPool
 */
public class PauseTaskPool extends TaskPool {
    private static class PauseTaskPoolInstance {
        public static final PauseTaskPool INSTANCE = new PauseTaskPool();
    }

    /**
     * PauseTaskPool
     */
    private PauseTaskPool() {
        super(CloudSingleton.cloudAppConfig.getPausePoolSize(),
                CloudSingleton.cloudAppConfig.getPauseHoldSize(), 60L);
    }

    /**
     * getInstance
     *
     * @return
     */
    public static PauseTaskPool getInstance() {
        return PauseTaskPoolInstance.INSTANCE;
    }
}