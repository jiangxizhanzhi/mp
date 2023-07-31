package cn.cyberpeace.mp.log.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.security.core.context.SecurityContext;

import java.util.concurrent.CountDownLatch;

/**
 * @BelongsPackage: cn.cyberpeace.mp.log.event
 * @Author: hhuang
 * @CreateTime: 2023/03/31  13:39
 * @Description: 日志调用auth模块事件
 * @Version: 1.0
 */
@Getter
public class LogAuthEvent extends ApplicationEvent {

    private CountDownLatch latch;

    private SecurityContext usercontext;


    public LogAuthEvent(Object source) {
        super(source);
    }

    public LogAuthEvent(Object source, CountDownLatch latch, SecurityContext usercontext) {
        super(source);
        this.latch = latch;
        this.usercontext = usercontext;
    }
}
