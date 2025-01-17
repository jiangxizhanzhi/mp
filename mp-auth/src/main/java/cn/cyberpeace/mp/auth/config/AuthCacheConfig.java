 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AuthCacheConfig.java.
 // File size 3403.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 14:51:55 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

 /**
  * 身份验证缓存配置
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Configuration
public class AuthCacheConfig {

     /**
      *
      */// default 30s
    public static final String CACHE_MANAGER_NORMAL = "authCacheManager_normal";

     /**
      *
      */// default 60s (normal + 30s)
    public static final String CACHE_MANAGER_MEDIUM = "authCacheManager_medium";

     /**
      *
      */// default 10m
    public static final String CACHE_MANAGER_LONG = "authCacheManager_long";

     /**
      *
      */// default 6h
    public static final String CACHE_MANAGER_6H = "authCacheManager_6H";

     /**
      *
      */// default 5min
    public static final String LOGIN_FAILURE = "login_failure";

     /**
      * 身份验证配置
      */
     @Autowired
    private AuthConfig authConfig;

     /**
      * 验证缓存管理器正常
      *
      * @return {@link CacheManager }
      */
     @Bean(name = CACHE_MANAGER_NORMAL)
    @Primary
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
      * 验证缓存管理器介质
      *
      * @return {@link CacheManager }
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
      * 验证缓存管理器长度
      *
      * @return {@link CacheManager }
      */
     @Bean(name = CACHE_MANAGER_LONG)
    public CacheManager authCacheManagerLong() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

     /**
      * 授权缓存管理器
      *
      * @return {@link CacheManager }
      */
     @Bean(name = CACHE_MANAGER_6H)
    public CacheManager authCacheManager6H() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(6, TimeUnit.HOURS).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

     /**
      * @return {@link Cache }<{@link String }, {@link Integer }>
      */
     @Bean(name = LOGIN_FAILURE)
    public Cache<String, Integer> loginFailureCache() {
        return Caffeine.newBuilder()
                // 设置最后一次写入或访问后经过固定时间过期
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
    }

}
