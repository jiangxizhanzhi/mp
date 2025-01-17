 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserEventPublisher.java.
 // File size 710.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.extensions.security.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

 /**
  * 用户事件发布者
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Component
public class UserEventPublisher implements ApplicationEventPublisherAware {

     /**
      * 应用程序事件发布者
      */
     private ApplicationEventPublisher applicationEventPublisher;

     /**
      * 设置应用程序事件发布者
      *
      * @param applicationEventPublisher 应用程序事件发布者
      */
     @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

     /**
      * 发布事件
      *
      * @param event 事件
      */
     public void publishEvent(Object event) {
        applicationEventPublisher.publishEvent(event);
    }

}
