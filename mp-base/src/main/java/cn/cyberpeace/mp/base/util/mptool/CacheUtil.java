package cn.cyberpeace.mp.base.util.mptool;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

public class CacheUtil {

    public static Cache<String, String> newTimedCache(long timeout) {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(timeout, TimeUnit.MILLISECONDS).build();
    }

}
