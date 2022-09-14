package cn.cyberpeace.mp.sceneplugingns3.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugingns3.constant.Gns3Singleton;

public class CreateTaskPool extends TaskPool {

    private static class CreateTaskPoolInstance {
        public static final CreateTaskPool INSTANCE = new CreateTaskPool();
    }

    private CreateTaskPool() {
        super(Gns3Singleton.gns3AppConfig.getCreatePoolSize(),
                Gns3Singleton.gns3AppConfig.getCreateHoldSize(), 60L);
    }

    public static CreateTaskPool getInstance() {
        return CreateTaskPoolInstance.INSTANCE;
    }
}
