 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysConfigUpdateEvent.java.
 // File size 557.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.event;


import org.springframework.context.ApplicationEvent;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/1 15:59
 */
public class SysConfigUpdateEvent extends ApplicationEvent {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public SysConfigUpdateEvent(Object source) {
        super(source);
    }
}
