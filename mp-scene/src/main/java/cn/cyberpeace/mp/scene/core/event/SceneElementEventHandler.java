package cn.cyberpeace.mp.scene.core.event;

import cn.cyberpeace.mp.base.annotation.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.event.base.BaseEventHandler;
import cn.cyberpeace.mp.scene.core.event.base.EventModel;
import cn.cyberpeace.mp.scene.model.SceneModel;

@ClassFieldClasses({
        EventModel.class,
})
public class SceneElementEventHandler extends BaseEventHandler<SceneModel, EventModel> {

    public SceneElementEventHandler(SceneModel target, int event) {
        super(target, event, SceneElementEventTransition.SCENE_ELEMENT_EVENT_TRANSITION);
    }
}
