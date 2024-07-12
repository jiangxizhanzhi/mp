 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ConfigEventPublisher.java.
 // File size 1044.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadmin.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * @ClassName ConfigEventPublisher
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/4/1 16:27
 * @Version 1.0.0
 * @see ApplicationEventPublisherAware
 */
@Component
public class ConfigEventPublisher implements ApplicationEventPublisherAware {

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
