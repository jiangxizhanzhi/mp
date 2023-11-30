package cn.cyberpeace.mp.base.util.thread;

import cn.cyberpeace.mp.base.constant.BaseSingleton;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 全局ScheduledThreadPool工具类
 * @Author qiaokang
 * @Date：2021/8/9 11:29
 */
@Slf4j
public class ThreadPoolUtils {

    public static final ThreadPoolExecutor DEVICE_PUSH_POOL = new ThreadPoolExecutor(30, 400, 10, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(10000), r -> {
                Thread namedThread = new Thread(r);
                namedThread.setName(String.format("device_log_push_thread-%d", namedThread.getId()));
                return namedThread;
            }, new ThreadPoolExecutor.AbortPolicy());

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
