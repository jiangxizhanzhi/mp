package cn.cyberpeace.mp.base.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * @Description: 全局ScheduledThreadPool工具类
 * @Author qiaokang
 * @Date：2021/8/9 11:29
 */
public class ScheduledThreadPoolUtils {

    private static class SingletonHolder {
        private static final ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("scheduled-thread-pool-%d").build();
        private static final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(50, namedThreadFactory);
    }

    private ScheduledThreadPoolUtils (){}

    public static ScheduledExecutorService getInstance() {
        return SingletonHolder.scheduledExecutorService;
    }

}
