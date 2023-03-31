package cn.cyberpeace.mp.log.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @BelongsPackage: cn.cyberpeace.mp.log.event
 * @Author: hhuang
 * @CreateTime: 2023/03/31  15:14
 * @Description: log调用auth模块处理类
 * @Version: 1.0
 */
@Component
public class LogAuthEventHandler {


    @Autowired
    private LogAuthEventPublisher logAuthEventPublisher;


    /**
     * @description: 调用auth模块响应auth相关数据
     * @param logAuth 
     * @return LogAuth
     */
    public LogAuth getLogAuthByCallAuthModule(LogAuth logAuth){
        CountDownLatch latch = new CountDownLatch(1);
        logAuthEventPublisher.publishEvent(new LogAuthEvent(logAuth,latch,SecurityContextHolder.getContext()));
        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return logAuth;
    }
}
