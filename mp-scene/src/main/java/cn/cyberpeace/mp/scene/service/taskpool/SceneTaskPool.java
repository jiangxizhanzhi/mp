 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneTaskPool.java.
 // File size 757.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.service.taskpool;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;

/**
 * @ClassName SceneTaskPool
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TaskPool
 */
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
