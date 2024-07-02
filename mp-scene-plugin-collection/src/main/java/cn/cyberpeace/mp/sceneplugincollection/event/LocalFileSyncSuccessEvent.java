package cn.cyberpeace.mp.sceneplugincollection.event;


import org.springframework.context.ApplicationEvent;

/**
 * @ClassName LocalFileSyncSuccessEvent
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ApplicationEvent
 */
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
