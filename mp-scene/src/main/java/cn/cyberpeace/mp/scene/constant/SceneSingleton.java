package cn.cyberpeace.mp.scene.constant;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.scene.config.SceneAppConfig;

public class SceneSingleton {

    public static final SceneAppConfig sceneAppConfig = BeanUtils.getBean(SceneAppConfig.class);

    private SceneSingleton() {}

}
