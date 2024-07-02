package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

/**
 * @ClassName StopTaskPool
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TaskPool
 */
public class StopTaskPool extends TaskPool {

    private static class StopTaskPoolInstance {
        public static final StopTaskPool INSTANCE = new StopTaskPool();
    }

    /**
     * StopTaskPool
     */
    private StopTaskPool() {
        super(CloudSingleton.cloudAppConfig.getStopPoolSize(),
            CloudSingleton.cloudAppConfig.getStopHoldSize(), 60L);
    }

    /**
     * getInstance
     *
     * @return
     */
    public static StopTaskPool getInstance() {
        return StopTaskPool.StopTaskPoolInstance.INSTANCE;
    }
}
