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
    RESTART("reboot", SceneElementModel.Event.RESTART),
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

    /**
     * 对应云平台接口提供的操作名称
     */
    private String action;

    /**
     * 对应 SceneElementModel.Event
     */
    private int event;

    ElementLifeCycleEnum(String action, int event) {
        this.action = action;
        this.event = event;
    }
}