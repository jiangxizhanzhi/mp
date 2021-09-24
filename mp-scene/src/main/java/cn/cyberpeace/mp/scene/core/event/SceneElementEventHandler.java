package cn.cyberpeace.mp.scene.core.event;

import cn.cyberpeace.mp.base.annotation.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.event.base.BaseEventHandler;
import cn.cyberpeace.mp.scene.core.event.base.EventModel;

@ClassFieldClasses({
        EventModel.class,
})
public class SceneElementEventHandler extends BaseEventHandler<SceneElementBO, EventModel> {

    public SceneElementEventHandler(SceneElementBO target, int event) {
        super(target, event, SceneElementEventTransition.SCENE_ELEMENT_EVENT_TRANSITION);
    }
}
