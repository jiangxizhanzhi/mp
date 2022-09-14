package cn.cyberpeace.mp.sceneplugingns3.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugingns3.constant.Gns3Singleton;

public class DeleteTaskPool extends TaskPool {

    private static class DeleteTaskPoolInstance {
        public static final DeleteTaskPool INSTANCE = new DeleteTaskPool();
    }

    private DeleteTaskPool() {
        super(Gns3Singleton.gns3AppConfig.getDeletePoolSize(),
                Gns3Singleton.gns3AppConfig.getDeleteHoldSize(), 60L);
    }

    public static DeleteTaskPool getInstance() {
        return DeleteTaskPoolInstance.INSTANCE;
    }
}
