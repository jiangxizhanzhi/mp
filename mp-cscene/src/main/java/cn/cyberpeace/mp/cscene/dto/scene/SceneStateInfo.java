package cn.cyberpeace.mp.cscene.dto.scene;

import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.model.SceneModel;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description
 * @Author maojun
 * @Date 2022/1/20
 */
@Slf4j
public class SceneStateInfo {
    private SceneModel sceneModel;


    public SceneStateInfo() {
    }

    public SceneStateInfo(SceneModel model) {
        this.sceneModel = model;
    }

    /**
     * 判断场景是否运行中
     * @return
     */
    public boolean isRunning() {
        if (sceneModel == null) {
            return false;
        }
        Integer event = sceneModel.getEvent();
        Integer eventStatus = sceneModel.getEventStatus();
        if (event == null || eventStatus == null) {
            log.error("scene model has no event or eventStatus,scene eid is {}", sceneModel.getId());
            return false;
        }
        if (sceneModel.hasDeleted()) {
            return false;
        }
        if (SceneModel.Event.DEFAULT == event || SceneModel.Event.INITIATE == event) {
            return false;
        }
        // 启动未完成的场景不属于运行中
        if (SceneModel.Event.CREATE == event && EventStatus.OVER != eventStatus) {
            return false;
        }
        // 暂停结束的场景不属于运行中
        if (SceneModel.Event.PAUSE == event && EventStatus.OVER == eventStatus) {
            return false;
        }
        // 挂起结束的场景不属于运行中
        if (SceneModel.Event.SUSPEND == event && EventStatus.OVER == eventStatus) {
            return false;
        }
        // 其余为运行中
        return true;
    }

}
