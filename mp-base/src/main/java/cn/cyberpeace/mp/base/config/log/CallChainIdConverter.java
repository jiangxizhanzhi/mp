package cn.cyberpeace.mp.base.config.log;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import cn.cyberpeace.mp.base.context.SysContextHolder;

/**
 * @Description 用于同步日志、控制台日志中打印追踪id
 * @Author maojun
 * @Date 2021/9/26
 */
public class CallChainIdConverter extends ClassicConverter {
    @Override
    public String convert(ILoggingEvent event) {
        return SysContextHolder.getCallChainId();
    }
}
