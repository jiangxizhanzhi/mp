package cn.cyberpeace.mp.scene.constant;

import cn.cyberpeace.mp.scene.model.SceneElementModel;
import lombok.Getter;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/21
 */
@Getter
public enum ElementLifeCycleEnum {
    /**
     * 重启节点
     */
    RESTART("restart", SceneElementModel.Event.RESTART),
    /**
     * 挂起节点
     */
    PAUSE("pause", SceneElementModel.Event.PAUSE),
    /**
     * 停止挂起
     */
    UNPAUSE("unpause", SceneElementModel.Event.UNPAUSE);

//    /**
//     * 重建节点
//     */
//    RECREATE("recreate", Integer.MAX_VALUE);

    private String action;

    private int event;

    ElementLifeCycleEnum(String action, int event) {
        this.action = action;
        this.event = event;
    }

    public static ElementLifeCycleEnum getBy(int event) {
        for (ElementLifeCycleEnum lifeCycleEnum : ElementLifeCycleEnum.values()) {
            if (lifeCycleEnum.event == event) {
                return lifeCycleEnum;
            }
        }
        return null;
    }
}