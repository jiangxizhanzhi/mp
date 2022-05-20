package cn.cyberpeace.mp.front.business.config;

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
@Configuration
public class FrontBizCacheConfig {

    public static final String CACHE_MANAGER_1M = "frontBizCacheManager_1M";
    public static final String CACHE_MANAGER_5M = "frontBizCacheManager_5M";
    public static final String CACHE_MANAGER_10M = "frontBizCacheManager_10M";


    @Bean(name = CACHE_MANAGER_1M)
    public CacheManager frontBizCacheManager1M() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }


    @Bean(name = CACHE_MANAGER_5M)
    public CacheManager frontBizCacheManager5M() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

    @Bean(name = CACHE_MANAGER_10M)
    public CacheManager frontBizCacheManager10M() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

}
