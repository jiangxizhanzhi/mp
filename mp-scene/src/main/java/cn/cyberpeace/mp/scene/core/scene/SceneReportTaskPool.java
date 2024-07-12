 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneReportTaskPool.java.
 // File size 818.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.scene;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;

/**
 * @ClassName SceneReportTaskPool
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TaskPool
 */
public class SceneReportTaskPool extends TaskPool {

    private static class SceneReportTaskPoolInstance {
        public static final SceneReportTaskPool INSTANCE = new SceneReportTaskPool();
    }

    private SceneReportTaskPool() {
        super(SceneSingleton.sceneAppConfig.getReportPoolSize(),
                SceneSingleton.sceneAppConfig.getReportHoldSize(), 60L);
    }

    public static SceneReportTaskPool getInstance() {
        return SceneReportTaskPoolInstance.INSTANCE;
    }

}
