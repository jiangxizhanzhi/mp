package cn.cyberpeace.mp.scene.core.event.dispatch;

import cn.cyberpeace.mp.scene.core.event.SceneEventTransition;
import cn.cyberpeace.mp.scene.core.event.base.EventTransition;
import cn.cyberpeace.mp.scene.core.event.interrupt.DoNextStrategy;
import cn.cyberpeace.mp.scene.core.event.interrupt.MpSceneOperateInterrupted;
import cn.cyberpeace.mp.scene.core.event.interrupt.UpdateLatestEventStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description 单机的场景事件控制
 * @Author maojun
 * @Date 2022/5/12
 */
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
            if (validEvents.contains(event)) {
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
        log.info("scene {} , event [{},{}] , try acquire scene event lock", sceneId, event, eventStatus);
        if (this.lock.tryLock()) {
            // 拿到锁
            if (this.latestEvent == null) {
                // 没有新的事件 则继续执行即可
                this.event = event;
                this.eventStatus = eventStatus;
                log.info("scene {} , event [{},{}] , acquire lock success and continue execute", sceneId, event, eventStatus);
                return true;
            } else {
                // 有新的事件 需要中断并执行下一个事件
                log.info("scene {} , event [{},{}] ,next event [{},{}], acquire lock success and trigger do next ", sceneId, event, eventStatus, latestEvent, latestEventStatus);
                throw new MpSceneOperateInterrupted(new DoNextStrategy());
            }
        } else {
            // 未拿到锁 根据事件表判断是否要更新下个事件
            log.info("scene {} , event [{},{}] , acquire lock success and trigger update latest event ", sceneId, event, eventStatus);
            throw new MpSceneOperateInterrupted(new UpdateLatestEventStrategy(event, eventStatus));
        }
    }

    public boolean releaseEventLock() {
        if (this.executorId.equals(String.valueOf(Thread.currentThread().getId()))) {
            log.info("scene {} , release event success", sceneId);
            try {
                // lock可能被上锁多次
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
