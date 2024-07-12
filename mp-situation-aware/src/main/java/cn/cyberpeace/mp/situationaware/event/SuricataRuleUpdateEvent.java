 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SuricataRuleUpdateEvent.java.
 // File size 336.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.event;

import org.springframework.context.ApplicationEvent;
public class SuricataRuleUpdateEvent extends ApplicationEvent {
    /**
     * SuricataRuleUpdateEvent
     *
     * @param source
     */
    public SuricataRuleUpdateEvent(Object source) {
        super(source);
    }
}
