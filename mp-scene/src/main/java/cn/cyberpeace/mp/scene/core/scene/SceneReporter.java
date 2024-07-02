package cn.cyberpeace.mp.scene.core.scene;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.scene.core.bo.CollectedSceneBO;
import cn.cyberpeace.mp.scene.core.bo.CollectedSceneElementBO;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName SceneReporter
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public interface SceneReporter {

    static List<SceneReporter> getReporters() {
        return new ArrayList<>(BeanCommon.getBeanType(SceneReporter.class).values());
    }

    void reportScene(CollectedSceneBO scene);

    void reportElement(CollectedSceneElementBO sceneElement);
}
