package cn.cyberpeace.mp.scene.service.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;

public class ElementTaskPool extends TaskPool {

    private static class ElementTaskPoolInstance {
        public static final ElementTaskPool INSTANCE = new ElementTaskPool();
    }

    private ElementTaskPool() {
        super(SceneSingleton.sceneAppConfig.getElementPoolSize(), SceneSingleton.sceneAppConfig.getElementHoldSize(), 60L);
    }

    public static ElementTaskPool getInstance() {
        return ElementTaskPoolInstance.INSTANCE;
    }

}
