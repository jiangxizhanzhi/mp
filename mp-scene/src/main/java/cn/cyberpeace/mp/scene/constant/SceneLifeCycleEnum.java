package cn.cyberpeace.mp.scene.constant;

import cn.cyberpeace.mp.scene.model.SceneModel;
import lombok.Getter;


@Getter
public enum SceneLifeCycleEnum {
    
    PAUSE(SceneModel.Event.PAUSE),
    
    UNPAUSE(SceneModel.Event.UNPAUSE);

    
    private int event;

    SceneLifeCycleEnum(int event) {
        this.event = event;
    }
}