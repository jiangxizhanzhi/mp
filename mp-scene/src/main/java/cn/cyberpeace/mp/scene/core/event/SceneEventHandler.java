package cn.cyberpeace.mp.scene.core.event;

import cn.cyberpeace.mp.base.annotation.ClassFieldClasses;
import cn.cyberpeace.mp.base.util.MessageUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scene.core.event.base.BaseEventHandler;
import cn.cyberpeace.mp.scene.core.event.base.EventModel;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;

@ClassFieldClasses({
        EventModel.class,
})
public class SceneEventHandler extends BaseEventHandler<SceneBO, EventModel> {

    public static final String EVENT_NAME_PREFIX = "scene.event.";

    public static final Map<Integer, String> EVENT_NAME_MAP = MapUtils.putAll(
            new HashMap<>(),
            new Object[]{
                    SceneModel.Event.DEFAULT, EVENT_NAME_PREFIX + "default",
                    SceneModel.Event.INITIATE, EVENT_NAME_PREFIX + "initiate",
                    SceneModel.Event.CREATE, EVENT_NAME_PREFIX + "create",
                    SceneModel.Event.START, EVENT_NAME_PREFIX + "start",
                    SceneModel.Event.STOP, EVENT_NAME_PREFIX + "stop",
                    SceneModel.Event.RESTART, EVENT_NAME_PREFIX + "restart",
                    SceneModel.Event.PAUSE, EVENT_NAME_PREFIX + "pause",
                    SceneModel.Event.UNPAUSE, EVENT_NAME_PREFIX + "unpause",
                    SceneModel.Event.SNAPSHOT, EVENT_NAME_PREFIX + "snapshot",
                    SceneModel.Event.DELETE, EVENT_NAME_PREFIX + "delete",
                    SceneModel.Event.MODIFY, EVENT_NAME_PREFIX + "modify",
            });

    public static final Map<Integer, String> ELEMENT_NAME_MAP = MapUtils.putAll(
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

    public SceneEventHandler(SceneBO target, int event) {
        super(target, event, EVENT_NAME_MAP.get(event), SceneEventTransition.SCENE_EVENT_TRANSITION);
    }

    @Override
    public String getTargetName() {
        return MessageUtils.get(EVENT_NAME_PREFIX + "scene", getTarget().getName());
    }
}
