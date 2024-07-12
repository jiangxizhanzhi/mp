 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneLifeCycleEnum.java.
 // File size 601.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.constant;

import cn.cyberpeace.mp.scene.model.SceneModel;
import lombok.Getter;

/**
 * @ClassName SceneLifeCycleEnum
 * @Description
 * @Author maojun
 * @Date 2021/10/21
 * @Version 1.0.0
 * @see Enum
 */
@Getter
public enum SceneLifeCycleEnum {
    /**
     * 暂停场景
     */
    PAUSE(SceneModel.Event.PAUSE),
    /**
     * 恢复场景
     */
    UNPAUSE(SceneModel.Event.UNPAUSE);

    /**
     * 对应 SceneModel.Event
     */
    private int event;

    SceneLifeCycleEnum(int event) {
        this.event = event;
    }
}