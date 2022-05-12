package cn.cyberpeace.mp.scene.core.event.interrupt;

import cn.cyberpeace.mp.scene.core.event.dispatch.SceneEventControllerUtils;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;

/**
 * @Description 更新场景下一个要执行的事件
 * @Author maojun
 * @Date 2022/5/12
 */
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
