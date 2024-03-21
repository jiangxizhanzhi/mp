package cn.cyberpeace.mp.base.util.thread;

import cn.cyberpeace.mp.base.context.transfercontext.TransferContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
public class ThreadUtils {

    public static Runnable format(Runnable task) {
        Map<String, Object> contextMap = TransferContext.getContextMap();
        return () -> {
            Map<String, Object> originContextMap = TransferContext.getContextMap();
            TransferContext.setContextMap(contextMap);
            task.run();
            TransferContext.setContextMap(originContextMap);
        };
    }

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
