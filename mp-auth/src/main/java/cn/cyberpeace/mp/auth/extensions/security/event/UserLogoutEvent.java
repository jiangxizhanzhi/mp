package cn.cyberpeace.mp.auth.extensions.security.event;


import org.springframework.context.ApplicationEvent;

/**
 * @ClassName UserLogoutEvent
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ApplicationEvent
 */
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
