package cn.cyberpeace.mp.scene.constant;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.scene.config.SceneAppConfig;

public class SceneSingleton {

    public static final SceneAppConfig sceneAppConfig = BeanCommon.getBean(SceneAppConfig.class);

    private SceneSingleton() {}

}
