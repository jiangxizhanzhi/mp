 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FrontBusinessCacheConfig.java.
 // File size 2157.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName FrontBusinessCacheConfig
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/5/16 11:05
 * @Version 1.0.0
 */
@Configuration
public class FrontBusinessCacheConfig {

    public static final String CACHE_MANAGER_1M = "frontBizCacheManager_1M";
    public static final String CACHE_MANAGER_5M = "frontBizCacheManager_5M";
    public static final String CACHE_MANAGER_10M = "frontBizCacheManager_10M";


    /**
     * frontBizCacheManager1M
     *
     * @return
     */
    @Bean(name = CACHE_MANAGER_1M)
    public CacheManager frontBizCacheManager1M() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }


    /**
     * frontBizCacheManager5M
     *
     * @return
     */
    @Bean(name = CACHE_MANAGER_5M)
    public CacheManager frontBizCacheManager5M() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

    /**
     * frontBizCacheManager10M
     *
     * @return
     */
    @Bean(name = CACHE_MANAGER_10M)
    public CacheManager frontBizCacheManager10M() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

}
