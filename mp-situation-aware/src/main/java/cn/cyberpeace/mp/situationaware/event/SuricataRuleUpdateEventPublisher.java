package cn.cyberpeace.mp.situationaware.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SuricataRuleUpdateEventPublisher {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void publishEvent(String source) {
        eventPublisher.publishEvent(new SuricataRuleUpdateEvent(source));
    }


}
