package cn.cyberpeace.mp.scene.core.scene;

import cn.cyberpeace.mp.base.util.thread.TaskPool;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;
import lombok.Data;

@Data
public class SceneReportTaskPool {

    private TaskPool taskPool;

    private static class SceneReportTaskPoolInstance {
        public static final SceneReportTaskPool INSTANCE = new SceneReportTaskPool();
    }

    private SceneReportTaskPool() {
        setTaskPool(new TaskPool(SceneSingleton.sceneAppConfig.getReportPoolSize(),
                SceneSingleton.sceneAppConfig.getReportHoldSize(), 60L));
    }

    public static SceneReportTaskPool getInstance() {
        return SceneReportTaskPoolInstance.INSTANCE;
    }


    public void executeTask(Runnable task) {
        taskPool.executeTask(task);
    }

}
