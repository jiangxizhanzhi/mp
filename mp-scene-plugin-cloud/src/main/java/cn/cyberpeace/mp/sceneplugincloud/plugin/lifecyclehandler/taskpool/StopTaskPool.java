package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

/**
 * @BelongsPackage: cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool
 * @Author: hhuang
 * @CreateTime: 2023/04/27  11:48
 * @Description: 关闭节点任务池
 * @Version: 1.0
 */
public class StopTaskPool extends TaskPool {

    private static class StopTaskPoolInstance {
        public static final StopTaskPool INSTANCE = new StopTaskPool();
    }

    private StopTaskPool() {
        super(CloudSingleton.cloudAppConfig.getStopPoolSize(),
            CloudSingleton.cloudAppConfig.getStopHoldSize(), 60L);
    }

    public static StopTaskPool getInstance() {
        return StopTaskPool.StopTaskPoolInstance.INSTANCE;
    }
}
