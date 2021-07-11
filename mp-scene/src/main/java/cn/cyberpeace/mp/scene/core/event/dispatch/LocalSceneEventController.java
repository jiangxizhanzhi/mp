package cn.cyberpeace.mp.scene.core.event.dispatch;

import cn.cyberpeace.mp.scene.core.event.SceneEventTransition;
import cn.cyberpeace.mp.scene.core.event.base.EventTransition;
import cn.cyberpeace.mp.scene.core.event.interrupt.DoNextStrategy;
import cn.cyberpeace.mp.scene.core.event.interrupt.MpSceneOperateInterrupted;
import cn.cyberpeace.mp.scene.core.event.interrupt.UpdateLatestEventStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;


@Slf4j
public class LocalSceneEventController implements SceneEventControl {
    private String executorId;
    private ReentrantLock lock;

    private String sceneId;
    
    private int event;
    private int eventStatus;
    
    private Integer latestEvent;
    private Integer latestEventStatus;

    public LocalSceneEventController(String sceneId, int event, int eventStatus) {
        this.sceneId = sceneId;
        this.executorId = String.valueOf(Thread.currentThread().getId());
        this.event = event;
        this.eventStatus = eventStatus;
        this.lock = new ReentrantLock();
    }

    public Integer getLatestEvent() {
        return this.latestEvent;
    }

    public boolean tryUpdateLatestEvent(int event, int eventStatus) {
        synchronized (this) {
            int compareEvent = this.latestEvent != null ? this.latestEvent : this.event;
            int compareEventStatus = this.latestEventStatus != null ? this.latestEventStatus : this.eventStatus;
            log.info("scene {} , try update latest event , [{},{}] , compare event [{},{}]", sceneId, event, eventStatus, compareEvent, compareEventStatus);
            var validEvents = SceneEventTransition.SCENE_EVENT_TRANSITION.getOrDefault(
                    EventTransition.genKey(compareEvent, compareEventStatus), new ArrayList<>()
            );
            if (validEvents.contains(event) && event != compareEvent) {
                this.latestEvent = event;
                this.latestEventStatus = eventStatus;
                log.info("scene {} , try update latest event success", sceneId, event, eventStatus);
                return true;
            }
            log.info("scene {} , try update latest event failed", sceneId, event, eventStatus);
            return false;
        }
    }

    public boolean acquireEventLock(int event, int eventStatus) {
        log.debug("scene {} , event [{},{}] , try acquire scene event lock", sceneId, event, eventStatus);
        if (this.lock.tryLock()) {
            
            if (this.latestEvent == null) {
                
                this.event = event;
                this.eventStatus = eventStatus;
                log.debug("scene {} , event [{},{}] , acquire lock success and continue execute", sceneId, event, eventStatus);
                return true;
            } else {
                
                log.info("scene {} , event [{},{}] ,next event [{},{}], acquire lock success and trigger do next ", sceneId, event, eventStatus, latestEvent, latestEventStatus);
                throw new MpSceneOperateInterrupted(new DoNextStrategy());
            }
        } else {
            
            log.info("scene {} , event [{},{}] , acquire lock success and trigger update latest event ", sceneId, event, eventStatus);
            throw new MpSceneOperateInterrupted(new UpdateLatestEventStrategy(event, eventStatus));
        }
    }

    public boolean releaseEventLock() {
        if (this.executorId.equals(String.valueOf(Thread.currentThread().getId()))) {
            log.info("scene {} , release event success", sceneId);
            try {
                
                for (; ; ) {
                    this.lock.unlock();
                }
            } catch (Exception e) {

            }
            return true;
        } else {
            log.info("scene {} , release event failed , not my lock", sceneId);
            return false;
        }
    }
}
