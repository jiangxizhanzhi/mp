package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/21
 */
public class LifeCycleControlTaskPool extends TaskPool {

    private static final LifeCycleControlTaskPool INSTANCE = new LifeCycleControlTaskPool();

    private LifeCycleControlTaskPool() {
        super(CloudSingleton.cloudAppConfig.getRecreatePoolSize(),
                CloudSingleton.cloudAppConfig.getRecreateHoldSize(), 60L);
    }

    public static LifeCycleControlTaskPool getInstance() {
        return LifeCycleControlTaskPool.INSTANCE;
    }
}