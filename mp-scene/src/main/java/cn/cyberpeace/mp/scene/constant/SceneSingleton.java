package cn.cyberpeace.mp.scene.constant;

import cn.cyberpeace.mp.scene.config.SceneAppConfig;
import org.springframework.stereotype.Component;

@Component
public class SceneSingleton {

    public static SceneAppConfig sceneAppConfig;

    private SceneSingleton(SceneAppConfig sceneAppConfig) {
        SceneSingleton.sceneAppConfig = sceneAppConfig;
    }

}
