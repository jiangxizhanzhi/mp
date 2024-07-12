 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneSingleton.java.
 // File size 493.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.constant;

import cn.cyberpeace.mp.scene.config.SceneAppConfig;
import org.springframework.stereotype.Component;

/**
 * @ClassName SceneSingleton
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Component
public class SceneSingleton {

    public static SceneAppConfig sceneAppConfig;

    private SceneSingleton(SceneAppConfig sceneAppConfig) {
        SceneSingleton.sceneAppConfig = sceneAppConfig;
    }

}
