package cn.cyberpeace.mp.base.config.log;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import cn.cyberpeace.mp.base.context.SysContextHolder;
import cn.cyberpeace.mp.base.util.ClassUtils;
import org.slf4j.MDC;

import java.lang.reflect.Field;

/**
 * @Description 异步打印日志, 向 eventObject 中加入追踪id
 * @Author maojun
 * @Date 2021/9/26
 */
public class LogAsyncAppender extends AsyncAppender {

    private static final Field mdcPropertyMapField = ClassUtils.getField(LoggingEvent.class, "mdcPropertyMap", false, false);

    @Override
    protected void preprocess(ILoggingEvent eventObject) {
        // 每次打印日志前设置 追踪id
        // 日志可能会在业务代码执行前打印,为避免在打印日志时创建线程上下文,需要加上SysContext是否存在的判断
        MDC.put("CALL_CHAIN_ID", SysContextHolder.getCallChainId());
        if (eventObject instanceof LoggingEvent) {
            // 需要先置空 mdcPropertyMap 字段,然后重新生成一次,否则获取不到上一步设置的CALL_CHAIN_ID
            var loggingEvent = (LoggingEvent) eventObject;
            ClassUtils.setFieldValue(mdcPropertyMapField, loggingEvent, null);
            loggingEvent.getMDCPropertyMap();
        }
        super.preprocess(eventObject);
    }
}
