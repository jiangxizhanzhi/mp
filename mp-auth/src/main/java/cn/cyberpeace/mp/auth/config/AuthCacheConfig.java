package cn.cyberpeace.mp.auth.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/16 11:05
 */
@EnableCaching
@Configuration
public class AuthCacheConfig {

    public static final String CACHE_MANAGER_20S = "authCacheManager_20S";
    //public static final String CACHE_MANAGER_30S = "authCacheManager_30S";
    public static final String CACHE_MANAGER_45S = "authCacheManager_45S";
    public static final String CACHE_MANAGER_1M = "authCacheManager_1M";
    //public static final String CACHE_MANAGER_5M = "authCacheManager_5M";
    public static final String CACHE_MANAGER_10M = "authCacheManager_10M";


    @Bean(name = CACHE_MANAGER_20S)
    public CacheManager authCacheManager20S() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(20, TimeUnit.SECONDS).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

    /*@Bean(name = CACHE_MANAGER_30S)
    public CacheManager authCacheManager30S() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }*/

    @Bean(name = CACHE_MANAGER_45S)
    public CacheManager authCacheManager45S() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(45, TimeUnit.SECONDS).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

    @Bean(name = CACHE_MANAGER_1M)
    public CacheManager authCacheManager1M() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }


    /*@Bean(name = CACHE_MANAGER_5M)
    public CacheManager authCacheManager5M() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }*/

    @Bean(name = CACHE_MANAGER_10M)
    public CacheManager authCacheManager10M() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

}
