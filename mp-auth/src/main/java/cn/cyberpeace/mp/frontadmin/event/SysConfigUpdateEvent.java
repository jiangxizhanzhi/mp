 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysConfigUpdateEvent.java.
 // File size 635.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadmin.event;


import org.springframework.context.ApplicationEvent;

/**
 * @ClassName SysConfigUpdateEvent
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/4/1 15:59
 * @Version 1.0.0
 * @see ApplicationEvent
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
