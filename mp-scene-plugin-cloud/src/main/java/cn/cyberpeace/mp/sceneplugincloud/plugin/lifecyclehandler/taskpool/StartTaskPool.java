package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

/**
 * @BelongsPackage: cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool
 * @Author: hhuang
 * @CreateTime: 2023/04/27  11:48
 * @Description: 启动节点任务池
 * @Version: 1.0
 */
public class StartTaskPool extends TaskPool {

    private static class StartTaskPoolInstance {
        public static final StartTaskPool INSTANCE = new StartTaskPool();
    }

    private StartTaskPool() {
        super(CloudSingleton.cloudAppConfig.getStartPoolSize(),
            CloudSingleton.cloudAppConfig.getStartHoldSize(), 60L);
    }

    public static StartTaskPool getInstance() {
        return StartTaskPool.StartTaskPoolInstance.INSTANCE;
    }
}
