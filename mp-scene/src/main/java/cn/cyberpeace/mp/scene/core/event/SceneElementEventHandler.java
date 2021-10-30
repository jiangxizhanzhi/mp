package cn.cyberpeace.mp.scene.core.event;

import cn.cyberpeace.mp.base.annotation.ClassFieldClasses;
import cn.cyberpeace.mp.base.util.MessageUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.event.base.BaseEventHandler;
import cn.cyberpeace.mp.scene.core.event.base.EventModel;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@ClassFieldClasses({
        EventModel.class,
})
public class SceneElementEventHandler extends BaseEventHandler<SceneElementBO, EventModel> {

    public static final Map<Integer, String> EVENT_NAME_MAP = MapUtils.putAll(
            new HashMap<>(),
            new Object[]{
                    SceneElementModel.Event.DEFAULT, EVENT_NAME_PREFIX + "default",
                    SceneElementModel.Event.INITIATE, EVENT_NAME_PREFIX + "initiate",
                    SceneElementModel.Event.CREATE, EVENT_NAME_PREFIX + "create",
                    SceneElementModel.Event.START, EVENT_NAME_PREFIX + "start",
                    SceneElementModel.Event.STOP, EVENT_NAME_PREFIX + "stop",
                    SceneElementModel.Event.RESTART, EVENT_NAME_PREFIX + "restart",
                    SceneElementModel.Event.PAUSE, EVENT_NAME_PREFIX + "pause",
                    SceneElementModel.Event.UNPAUSE, EVENT_NAME_PREFIX + "unpause",
                    SceneElementModel.Event.SNAPSHOT, EVENT_NAME_PREFIX + "snapshot",
                    SceneElementModel.Event.DELETE, EVENT_NAME_PREFIX + "delete",
                    SceneElementModel.Event.MODIFY, EVENT_NAME_PREFIX + "modify",
                    SceneElementModel.Event.RECREATE, EVENT_NAME_PREFIX + "recreate",
            });

    public SceneElementEventHandler(SceneElementBO target, int event) {
        super(target, event, EVENT_NAME_MAP.get(event), SceneElementEventTransition.SCENE_ELEMENT_EVENT_TRANSITION);
    }

    @Override
    public String getTargetName() {
        var targetName = getTarget().getName();
        if (StringUtils.isEmpty(targetName)) {
            targetName = getTarget().getId();
        }
        return MessageUtils.get(SceneEventHandler.EVENT_NAME_PREFIX + "scene_element", targetName);
    }
}
