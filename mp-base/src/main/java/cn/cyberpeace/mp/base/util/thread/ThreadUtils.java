package cn.cyberpeace.mp.base.util.thread;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.context.transfercontext.TransferContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

@Slf4j
public class ThreadUtils {

    public static Runnable format(Runnable task) {
        var contextMap = TransferContext.getContextMap();
        String t1 = Thread.currentThread().getName();
        return () -> {
            var originContextMap = TransferContext.getContextMap();
            String t2 = Thread.currentThread().getName();
            log.info("thread pass through: from {} to {},origin is {},now is {}", t1, t2, JsonCommon.stringify(originContextMap), JsonCommon.stringify(contextMap));
            TransferContext.setContextMap(contextMap);
            task.run();
            TransferContext.setContextMap(originContextMap);
        };
    }

    public static <T> Callable<T> format(Callable<T> task) {
        var contextMap = TransferContext.getContextMap();
        return  () -> {
            var originContextMap = TransferContext.getContextMap();
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
