 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneEventHandler.java.
 // File size 3173.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.event;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scene.core.event.base.BaseEventHandler;
import cn.cyberpeace.mp.scene.core.event.base.EventModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName SceneEventHandler
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseEventHandler
 */
public class SceneEventHandler extends BaseEventHandler<SceneBO, EventModel> {

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
                    SceneModel.Event.SUSPEND, EVENT_NAME_PREFIX + "suspend",
                    SceneModel.Event.RESUME, EVENT_NAME_PREFIX + "resume",
                    SceneModel.Event.SNAPSHOT, EVENT_NAME_PREFIX + "snapshot",
                    SceneModel.Event.DELETE, EVENT_NAME_PREFIX + "delete",
                    SceneModel.Event.MODIFY, EVENT_NAME_PREFIX + "modify",
                    SceneModel.Event.RECREATE, EVENT_NAME_PREFIX + "recreate",
                    SceneModel.Event.REVERTSNAPSHOT, EVENT_NAME_PREFIX + "revertsnapshot",
            });

    public SceneEventHandler(SceneBO target, int event) {
        super(target, event, EVENT_NAME_MAP.get(event), SceneEventTransition.SCENE_EVENT_TRANSITION);
    }

    @Override
    public String getTargetName() {
        return MessageCommon.get(EVENT_NAME_PREFIX + "scene", getTarget().getName());
    }

    @Override
    protected void logProcess(int event, int eventStatus, String process, int processStatus, String processMessage) {
        List<String> formatMessage = new ArrayList<>();
        formatMessage.add(getTarget().getEid());
        formatMessage.add(StringUtils.EMPTY);
        formatMessage.add(getEventName());
        formatMessage.add(String.valueOf(eventStatus));
        formatMessage.add(process);
        formatMessage.add(String.valueOf(processStatus));
        String message = "[" + this.getTarget().getStage() + "," + this.getTarget().getStagePercentage() + "]" + processMessage + " process[" + StringUtils.join(formatMessage, ",") + "]";
        super.logProcess(event, eventStatus, process, processStatus, message);
    }

    @Override
    protected void initClass() {
        super.eventModelClass = EventModel.class;
    }
}
