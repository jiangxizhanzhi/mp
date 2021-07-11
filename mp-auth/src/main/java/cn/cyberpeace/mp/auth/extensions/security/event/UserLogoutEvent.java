package cn.cyberpeace.mp.auth.extensions.security.event;


import org.springframework.context.ApplicationEvent;

public class UserLogoutEvent extends ApplicationEvent {

    
    public UserLogoutEvent(Object source) {
        super(source);
    }
}
