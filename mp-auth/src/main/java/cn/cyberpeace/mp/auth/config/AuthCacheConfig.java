package cn.cyberpeace.mp.auth.config;

import cn.cyberpeace.mp.auth.config.security.AuthenticateConfig;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/16 11:05
 */
@Configuration
public class AuthCacheConfig {

    // default 30s
    public static final String CACHE_MANAGER_NORMAL = "authCacheManager_normal";

    // default 60s (normal + 30s)
    public static final String CACHE_MANAGER_MEDIUM = "authCacheManager_medium";

    // default 10m
    public static final String CACHE_MANAGER_LONG = "authCacheManager_long";

    // default 6h
    public static final String CACHE_MANAGER_6H = "authCacheManager_6H";

    @Autowired
    private AuthenticateConfig authenticateConfig;


    @Bean(name = CACHE_MANAGER_NORMAL)
    public CacheManager authCacheManagerNormal() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Integer seconds = authenticateConfig.getCacheTime();
        if (Objects.isNull(seconds) || seconds < 1) {
            seconds = 30;
        }
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(seconds, TimeUnit.SECONDS).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

    @Bean(name = CACHE_MANAGER_MEDIUM)
    public CacheManager authCacheManagerMedium() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Integer seconds = authenticateConfig.getCacheTime();
        if (Objects.isNull(seconds) || seconds < 1) {
            seconds = 30;
        }
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(seconds + 30, TimeUnit.SECONDS).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

    @Bean(name = CACHE_MANAGER_LONG)
    public CacheManager authCacheManagerLong() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

    @Bean(name = CACHE_MANAGER_6H)
    public CacheManager authCacheManager6H() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(6, TimeUnit.HOURS).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

}
