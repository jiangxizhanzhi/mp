package cn.cyberpeace.mp.sceneplugincollection.event;


import org.springframework.context.ApplicationEvent;

public class LocalFileSyncSuccessEvent extends ApplicationEvent {

    /**
     * LocalFileSyncSuccessEvent
     *
     * @param source
     */
    public LocalFileSyncSuccessEvent(Object source) {
        super(source);
    }
}
