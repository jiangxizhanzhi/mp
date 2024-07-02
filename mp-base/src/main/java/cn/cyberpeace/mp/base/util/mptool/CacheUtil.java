package cn.cyberpeace.mp.base.util.mptool;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName CacheUtil
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class CacheUtil {

    /**
     * 创建定时缓存.
     * Params:
     * timeout – 过期时长，单位：毫秒
     */
    public static Cache<String, String> newTimedCache(long timeout) {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(timeout, TimeUnit.MILLISECONDS).build();
    }

}
