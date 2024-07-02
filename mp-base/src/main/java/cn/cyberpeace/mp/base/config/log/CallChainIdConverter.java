package cn.cyberpeace.mp.base.config.log;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import cn.cyberpeace.mp.base.context.SysContextHolder;

/**
 * @ClassName CallChainIdConverter
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ClassicConverter
 */
public class CallChainIdConverter extends ClassicConverter {
    /**
     * convert
     *
     * @param event
     * @return
     */
    @Override
    public String convert(ILoggingEvent event) {
        return SysContextHolder.getCallChainId();
    }
}
