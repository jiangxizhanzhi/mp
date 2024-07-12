 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneReporter.java.
 // File size 684.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
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
