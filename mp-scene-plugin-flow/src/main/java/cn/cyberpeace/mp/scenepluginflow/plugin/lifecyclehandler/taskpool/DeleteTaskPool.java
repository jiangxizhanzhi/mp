package cn.cyberpeace.mp.scenepluginflow.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scenepluginflow.constant.FlowSingleton;

public class DeleteTaskPool extends TaskPool {

    private static class DeleteTaskPoolInstance {
        public static final DeleteTaskPool INSTANCE = new DeleteTaskPool();
    }

    private DeleteTaskPool() {
        super(FlowSingleton.flowAppConfig.getDeletePoolSize(),
                FlowSingleton.flowAppConfig.getDeleteHoldSize(), 60L);
    }

    public static DeleteTaskPool getInstance() {
        return DeleteTaskPoolInstance.INSTANCE;
    }

}
