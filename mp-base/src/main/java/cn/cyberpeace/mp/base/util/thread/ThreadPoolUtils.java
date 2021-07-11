package cn.cyberpeace.mp.base.util.thread;

import cn.cyberpeace.mp.base.constant.BaseSingleton;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;


public class ThreadPoolUtils {

    private static class SingletonHolder {
        private static final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(
                BaseSingleton.baseAppConfig.getScheduledThreadPoolCoreSize(),
                new ThreadFactoryBuilder().setNameFormat("scheduled-thread-pool-%d").build());
    }

    private ThreadPoolUtils(){}

    public static ScheduledExecutorService getScheduledExecutorService() {
        return SingletonHolder.scheduledExecutorService;
    }

}
