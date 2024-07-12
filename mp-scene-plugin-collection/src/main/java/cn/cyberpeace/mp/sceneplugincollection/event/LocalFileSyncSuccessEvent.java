 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LocalFileSyncSuccessEvent.java.
 // File size 508.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
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
