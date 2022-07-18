package cn.cyberpeace.mp.base.event;


import org.springframework.context.ApplicationEvent;

public class LocalFileSyncSuccessEvent extends ApplicationEvent {

    public LocalFileSyncSuccessEvent(Object source) {
        super(source);
    }
}
