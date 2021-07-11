package cn.cyberpeace.mp.frontadmin.event;


import org.springframework.context.ApplicationEvent;


public class SysConfigUpdateEvent extends ApplicationEvent {

    
    public SysConfigUpdateEvent(Object source) {
        super(source);
    }
}
