 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SuricataRuleUpdateEventPublisher.java.
 // File size 570.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SuricataRuleUpdateEventPublisher {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * publishEvent
     *
     * @param source
     */
    public void publishEvent(String source) {
        eventPublisher.publishEvent(new SuricataRuleUpdateEvent(source));
    }


}
