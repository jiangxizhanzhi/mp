package cn.cyberpeace.mp.flow.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.function.Consumer;

/**
 * @Description: 流量缓存工具类
 * @Author qiaokang
 * @Date：2022/11/16 11:28
 */
public class FlowCacheUtil {

    /**
     * consummer前缀
     */
    public static final String CONSUMER_PREFIX = "CONSUMER";

    /**
     * runnable前缀
     */
    public static final String RUNNABLE_PREFIX = "RUNNABLE";

    /**
     * pcap包解析缓存
     */
    public static Cache<String, String> pcapAnalyseCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

    /**
     * 任务runnable缓存
     */
    public static Cache<String, Runnable> taskRunnableCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

    /**
     * 任务consumer缓存
     */
    public static Cache<String, Consumer> taskConsumerCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

}
