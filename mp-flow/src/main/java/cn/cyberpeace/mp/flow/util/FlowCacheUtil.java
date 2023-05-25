package cn.cyberpeace.mp.flow.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * @Description: 流量缓存工具类
 * @Author qiaokang
 * @Date：2022/11/16 11:28
 */
public class FlowCacheUtil {

    public static Cache<String, String> pcapAnalyseCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

}
