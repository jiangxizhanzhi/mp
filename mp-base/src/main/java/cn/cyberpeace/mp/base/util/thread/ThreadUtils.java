package cn.cyberpeace.mp.base.util.thread;

import cn.cyberpeace.mp.base.context.transfercontext.TransferContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @ClassName ThreadUtils
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
public class ThreadUtils {

    /**
     * format
     *
     * @param task
     * @return
     */
    public static Runnable format(Runnable task) {
        Map<String, Object> contextMap = TransferContext.getContextMap();
        return () -> {
            Map<String, Object> originContextMap = TransferContext.getContextMap();
            TransferContext.setContextMap(contextMap);
            task.run();
            TransferContext.setContextMap(originContextMap);
        };
    }

    /**
     * format
     *
     * @param task
     * @return
     */
    public static <T> Callable<T> format(Callable<T> task) {
        Map<String, Object> contextMap = TransferContext.getContextMap();
        return  () -> {
            Map<String, Object> originContextMap = TransferContext.getContextMap();
            TransferContext.setContextMap(contextMap);
            try {
                return task.call();
            } catch (Exception e) {
                throw e;
            } finally {
                TransferContext.setContextMap(originContextMap);
            }
        };
    }
}
