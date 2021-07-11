package cn.cyberpeace.mp.base.config.log;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import cn.cyberpeace.mp.base.context.SysContextHolder;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import org.slf4j.MDC;

import java.lang.reflect.Field;

public class LogAsyncAppender extends AsyncAppender {

    private static final Field mdcPropertyMapField = ClassUtils.getField(LoggingEvent.class, "mdcPropertyMap", false, false);

    @Override
    protected void preprocess(ILoggingEvent eventObject) {
        
        
        MDC.put("CALL_CHAIN_ID", SysContextHolder.getCallChainId());
        if (eventObject instanceof LoggingEvent) {
            
            var loggingEvent = (LoggingEvent) eventObject;
            ClassUtils.setFieldValue(mdcPropertyMapField, loggingEvent, null);
            loggingEvent.getMDCPropertyMap();
        }
        super.preprocess(eventObject);
    }
}
