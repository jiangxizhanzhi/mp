package cn.cyberpeace.mp.front.admin.event;


import org.springframework.context.ApplicationEvent;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/1 15:59
 */
public class SysConfigUpdateEvent extends ApplicationEvent {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public SysConfigUpdateEvent(Object source) {
        super(source);
    }
}
