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
