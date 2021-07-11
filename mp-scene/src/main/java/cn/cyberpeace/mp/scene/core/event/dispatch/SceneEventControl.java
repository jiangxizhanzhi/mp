package cn.cyberpeace.mp.scene.core.event.dispatch;


public interface SceneEventControl {
    
    Integer getLatestEvent();

    
    boolean tryUpdateLatestEvent(int event, int eventStatus);

    
    boolean acquireEventLock(int event, int eventStatus);

    
    boolean releaseEventLock();
}
