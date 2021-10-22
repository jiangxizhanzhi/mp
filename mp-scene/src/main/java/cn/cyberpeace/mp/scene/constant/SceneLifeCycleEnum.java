package cn.cyberpeace.mp.scene.constant;

import cn.cyberpeace.mp.scene.model.SceneModel;
import lombok.Getter;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/21
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