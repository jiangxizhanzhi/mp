package cn.cyberpeace.mp.sceneplugincollection.event;


import org.springframework.context.ApplicationEvent;

public class LocalFileSyncSuccessEvent extends ApplicationEvent {

    public LocalFileSyncSuccessEvent(Object source) {
        super(source);
    }
}
