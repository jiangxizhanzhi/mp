package cn.cyberpeace.mp.scene.constant;

import cn.cyberpeace.mp.scene.model.SceneElementModel;
import lombok.Getter;


@Getter
public enum ElementLifeCycleEnum {
    
    RESTART("reboot", SceneElementModel.Event.RESTART),
    
    PAUSE("pause", SceneElementModel.Event.PAUSE),
    
    UNPAUSE("unpause", SceneElementModel.Event.UNPAUSE);




    
    private String action;

    
    private int event;

    ElementLifeCycleEnum(String action, int event) {
        this.action = action;
        this.event = event;
    }
}