package cn.cyberpeace.mp.base.util.thread;

import cn.cyberpeace.mp.base.context.transfercontext.TransferContext;

import java.util.concurrent.Callable;

public class ThreadUtils {

    public static Runnable format(Runnable task) {
        var contextMap = TransferContext.getContextMap();
        return  () -> {
            var originContextMap = TransferContext.getContextMap();
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
