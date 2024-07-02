package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

/**
 * @ClassName LinkStatusTaskPool
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TaskPool
 */
public class LinkStatusTaskPool extends TaskPool {

    private static class CreateTaskPoolInstance {
        public static final LinkStatusTaskPool INSTANCE = new LinkStatusTaskPool();
    }

    /**
     * LinkStatusTaskPool
     */
    private LinkStatusTaskPool() {
        super(CloudSingleton.cloudAppConfig.getLinkStatusPoolSize(),
                CloudSingleton.cloudAppConfig.getLinkStatusHoldSize(), 60L);
    }

    /**
     * getInstance
     *
     * @return
     */
    public static LinkStatusTaskPool getInstance() {
        return CreateTaskPoolInstance.INSTANCE;
    }
}
