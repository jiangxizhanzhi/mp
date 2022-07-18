package cn.cyberpeace.mp.front.admin.event;


import org.springframework.context.ApplicationEvent;

public class HighRiskAttachEvent extends ApplicationEvent {

    /**
     * Create a new {@code ApplicationEvent}.
     * @param source the object on which the event initially occurred or with
     *         which the event is associated (never {@code null})
     */
    public HighRiskAttachEvent(Object source) {
        super(source);
    }
}
