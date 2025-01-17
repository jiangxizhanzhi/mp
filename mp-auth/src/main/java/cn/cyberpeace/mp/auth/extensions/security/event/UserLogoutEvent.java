 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserLogoutEvent.java.
 // File size 467.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.extensions.security.event;


import org.springframework.context.ApplicationEvent;

public class UserLogoutEvent extends ApplicationEvent {

    /**
     * Create a new {@code ApplicationEvent}.
     * @param source the object on which the event initially occurred or with
     *         which the event is associated (never {@code null})
     */
    public UserLogoutEvent(Object source) {
        super(source);
    }
}
