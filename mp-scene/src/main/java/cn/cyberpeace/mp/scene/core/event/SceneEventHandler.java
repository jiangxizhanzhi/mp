package cn.cyberpeace.mp.scene.core.event;

import cn.cyberpeace.mp.base.annotation.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scene.core.event.base.BaseEventHandler;
import cn.cyberpeace.mp.scene.core.event.base.EventModel;

@ClassFieldClasses({
        EventModel.class,
})
public class SceneEventHandler extends BaseEventHandler<SceneBO, EventModel> {

    public SceneEventHandler(SceneBO target, int event) {
        super(target, event, SceneEventTransition.SCENE_EVENT_TRANSITION);
    }
}
