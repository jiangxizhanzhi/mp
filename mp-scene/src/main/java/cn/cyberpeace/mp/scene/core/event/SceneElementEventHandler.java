package cn.cyberpeace.mp.scene.core.event;

import cn.cyberpeace.mp.base.annotation.ClassFieldClasses;
import cn.cyberpeace.mp.base.util.MessageUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.event.base.BaseEventHandler;
import cn.cyberpeace.mp.scene.core.event.base.EventModel;

import java.util.Map;

@ClassFieldClasses({
        EventModel.class,
})
public class SceneElementEventHandler extends BaseEventHandler<SceneElementBO, EventModel> {

    public static final Map<Integer, String> EVENT_NAME_MAP = SceneEventHandler.EVENT_NAME_MAP;

    public SceneElementEventHandler(SceneElementBO target, int event) {
        super(target, event, EVENT_NAME_MAP.get(event), SceneElementEventTransition.SCENE_ELEMENT_EVENT_TRANSITION);
    }

    @Override
    public String getTargetName() {
        return MessageUtils.get(SceneEventHandler.EVENT_NAME_PREFIX + "scene_element", getTarget().getName());
    }
}
