package cn.cyberpeace.mp.cscene.dto.scene;

import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.model.SceneModel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
public class SceneStateInfo {
    private SceneModel sceneModel;


    public SceneStateInfo() {
    }

    public SceneStateInfo(SceneModel model) {
        this.sceneModel = model;
    }

    
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
        
        if (SceneModel.Event.CREATE == event && EventStatus.OVER != eventStatus) {
            return false;
        }
        
        if (SceneModel.Event.PAUSE == event && EventStatus.OVER == eventStatus) {
            return false;
        }
        
        if (SceneModel.Event.SUSPEND == event && EventStatus.OVER == eventStatus) {
            return false;
        }
        
        return true;
    }

}
