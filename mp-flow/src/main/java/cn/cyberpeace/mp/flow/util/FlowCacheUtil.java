package cn.cyberpeace.mp.flow.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.function.Consumer;


public class FlowCacheUtil {

    
    public static final String CONSUMER_PREFIX = "CONSUMER";

    
    public static final String RUNNABLE_PREFIX = "RUNNABLE";

    
    public static Cache<String, String> pcapAnalyseCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

    
    public static Cache<String, Runnable> taskRunnableCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

    
    public static Cache<String, Consumer> taskConsumerCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

}
