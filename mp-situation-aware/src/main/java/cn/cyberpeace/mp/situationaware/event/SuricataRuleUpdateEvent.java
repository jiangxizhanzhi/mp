package cn.cyberpeace.mp.situationaware.event;

import org.springframework.context.ApplicationEvent;
public class SuricataRuleUpdateEvent extends ApplicationEvent {
    public SuricataRuleUpdateEvent(Object source) {
        super(source);
    }
}
