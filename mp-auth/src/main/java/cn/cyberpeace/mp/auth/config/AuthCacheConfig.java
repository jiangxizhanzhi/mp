 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AuthCacheConfig.java.
 // File size 3734.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName AuthCacheConfig
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/5/16 11:05
 * @Version 1.0.0
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

    // default 5min
    public static final String LOGIN_FAILURE = "login_failure";

    @Autowired
    private AuthConfig authConfig;


    /**
     * authCacheManagerNormal
     *
     * @return
     */
    @Bean(name = CACHE_MANAGER_NORMAL)
    public CacheManager authCacheManagerNormal() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Integer seconds = authConfig.getAuthenticateCacheTime();
        if (Objects.isNull(seconds) || seconds < 1) {
            seconds = 30;
        }
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(seconds, TimeUnit.SECONDS).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

    /**
     * authCacheManagerMedium
     *
     * @return
     */
    @Bean(name = CACHE_MANAGER_MEDIUM)
    public CacheManager authCacheManagerMedium() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Integer seconds = authConfig.getAuthenticateCacheTime();
        if (Objects.isNull(seconds) || seconds < 1) {
            seconds = 30;
        }
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(seconds + 30, TimeUnit.SECONDS).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

    /**
     * authCacheManagerLong
     *
     * @return
     */
    @Bean(name = CACHE_MANAGER_LONG)
    public CacheManager authCacheManagerLong() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

    /**
     * authCacheManager6H
     *
     * @return
     */
    @Bean(name = CACHE_MANAGER_6H)
    public CacheManager authCacheManager6H() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(6, TimeUnit.HOURS).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

    /**
     * loginFailureCache
     *
     * @return
     */
    @Bean(name = LOGIN_FAILURE)
    public Cache<String, Integer> loginFailureCache() {
        return Caffeine.newBuilder()
                // 设置最后一次写入或访问后经过固定时间过期
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
    }

}
