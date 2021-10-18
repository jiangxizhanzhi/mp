package cn.cyberpeace.mp.scene.service.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;

public class SceneTaskPool extends TaskPool {

    private static class SceneTaskPoolInstance {
        public static final SceneTaskPool INSTANCE = new SceneTaskPool();
    }

    private SceneTaskPool() {
        super(SceneSingleton.sceneAppConfig.getScenePoolSize(), SceneSingleton.sceneAppConfig.getSceneHoldSize(), 60L);
    }

    public static SceneTaskPool getInstance() {
        return SceneTaskPoolInstance.INSTANCE;
    }

}
