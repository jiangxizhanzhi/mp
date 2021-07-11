package cn.cyberpeace.mp.scene.core.event.interrupt;

import cn.cyberpeace.mp.scene.core.event.dispatch.SceneEventControllerUtils;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;


public class UpdateLatestEventStrategy implements InterruptedStrategy {
    private int event;
    private int eventStatus;

    public UpdateLatestEventStrategy(int event, int eventStatus) {
        this.event = event;
        this.eventStatus = eventStatus;
    }

    @Override
    public void action(String sceneId, SceneHandler sceneHandler) {
        SceneEventControllerUtils.tryUpdateLatestEvent(sceneId, this.event, this.eventStatus);
    }
}
