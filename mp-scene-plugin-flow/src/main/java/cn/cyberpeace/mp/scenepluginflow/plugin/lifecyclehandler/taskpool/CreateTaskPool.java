package cn.cyberpeace.mp.scenepluginflow.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scenepluginflow.constant.FlowSingleton;

public class CreateTaskPool extends TaskPool {

    private static class CreateTaskPoolInstance {
        public static final CreateTaskPool INSTANCE = new CreateTaskPool();
    }

    private CreateTaskPool() {
        super(FlowSingleton.flowAppConfig.getCreatePoolSize(),
                FlowSingleton.flowAppConfig.getCreateHoldSize(), 60L);
    }

    public static CreateTaskPool getInstance() {
        return CreateTaskPoolInstance.INSTANCE;
    }
}
