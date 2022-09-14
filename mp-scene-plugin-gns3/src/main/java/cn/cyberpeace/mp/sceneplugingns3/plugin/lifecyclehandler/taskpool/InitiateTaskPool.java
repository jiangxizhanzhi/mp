package cn.cyberpeace.mp.sceneplugingns3.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.sceneplugingns3.constant.Gns3Singleton;

public class InitiateTaskPool extends TaskPool {

    private static class InitiateTaskPoolInstance {
        public static final InitiateTaskPool INSTANCE = new InitiateTaskPool();
    }

    private InitiateTaskPool() {
        super(Gns3Singleton.gns3AppConfig.getInitiatePoolSize(),
                Gns3Singleton.gns3AppConfig.getInitiateHoldSize(), 60L);
    }

    public static InitiateTaskPool getInstance() {
        return InitiateTaskPoolInstance.INSTANCE;
    }

}
