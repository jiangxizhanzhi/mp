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
