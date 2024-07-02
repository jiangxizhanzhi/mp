package cn.cyberpeace.mp.sceneplugincollection.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * @ClassName LocalFileSyncEventPublisher
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ApplicationEventPublisherAware
 */
@Component
public class LocalFileSyncEventPublisher implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * setApplicationEventPublisher
     *
     * @param applicationEventPublisher
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * publishEvent
     *
     * @param event
     */
    public void publishEvent(Object event) {
        applicationEventPublisher.publishEvent(event);
    }

}
