package cn.cyberpeace.mp.base.config.log;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import cn.cyberpeace.mp.base.context.SysContextHolder;

public class CallChainIdConverter extends ClassicConverter {
    @Override
    public String convert(ILoggingEvent event) {
        return SysContextHolder.getCallChainId();
    }
}
