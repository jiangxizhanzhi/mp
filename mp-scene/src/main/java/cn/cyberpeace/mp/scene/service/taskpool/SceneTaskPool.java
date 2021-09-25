package cn.cyberpeace.mp.scene.service.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;
import lombok.Data;

@Data
public class SceneTaskPool {

    private TaskPool taskPool;

    private static class SceneTaskPoolInstance {
        public static final SceneTaskPool INSTANCE = new SceneTaskPool();
    }

    private SceneTaskPool() {
        setTaskPool(new TaskPool(SceneSingleton.sceneAppConfig.getScenePoolSize(),
                SceneSingleton.sceneAppConfig.getSceneHoldSize(), 60L));
    }

    public static SceneTaskPool getInstance() {
        return SceneTaskPoolInstance.INSTANCE;
    }

    public void executeTask(Runnable task) {
        taskPool.executeTask(task);
    }

}
