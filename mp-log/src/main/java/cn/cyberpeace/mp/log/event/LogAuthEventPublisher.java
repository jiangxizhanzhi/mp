package cn.cyberpeace.mp.log.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * @BelongsPackage: cn.cyberpeace.mp.log.event
 * @Author: hhuang
 * @CreateTime: 2023/03/31  12:02
 * @Description: 日志模块调用auth模块发布事件
 * @Version: 1.0
 */
@Component
public class LogAuthEventPublisher implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * @description: 事件发布核心方法
     * @param event
     * @return void
     */
    public void publishEvent(Object event) {
        applicationEventPublisher.publishEvent(event);
    }
}
