package cn.cyberpeace.mp.base.config.log;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import cn.cyberpeace.mp.base.context.SysContextUtils;
import cn.cyberpeace.mp.base.util.ClassUtils;
import org.slf4j.MDC;

import java.lang.reflect.Field;

/**
 * @Description 异步打印日志,向 eventObject 中加入追踪id
 * @Author maojun
 * @Date 2021/9/26
 */
public class MpAsyncAppender extends AsyncAppender {

    private static Field mdcPropertyMapField = ClassUtils.getField(LoggingEvent.class, "mdcPropertyMap", false, false);

    @Override
    protected void preprocess(ILoggingEvent eventObject) {
        // 每次打印日志前设置 追踪id
        MDC.put("CALL_CHAIN_ID", SysContextUtils.getCallChainId());
        if (eventObject instanceof LoggingEvent) {
            // 需要先置空 mdcPropertyMap 字段,然后重新生成一次,否则获取不到上一步设置的CALL_CHAIN_ID
            LoggingEvent loggingEvent = (LoggingEvent) eventObject;
            ClassUtils.setFieldValue(mdcPropertyMapField, loggingEvent, null);
            loggingEvent.getMDCPropertyMap();
        }
        super.preprocess(eventObject);
    }
}
