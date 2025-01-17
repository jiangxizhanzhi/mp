 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FrontBusinessCacheConfig.java.
 // File size 1881.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

 /**
  * 前面业务缓存配置
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Configuration
public class FrontBusinessCacheConfig {

     /**
      * 缓存管理器1m
      */
     public static final String CACHE_MANAGER_1M = "frontBizCacheManager_1M";
     /**
      * 缓存管理器5m
      */
     public static final String CACHE_MANAGER_5M = "frontBizCacheManager_5M";
     /**
      * 缓存管理器10m
      */
     public static final String CACHE_MANAGER_10M = "frontBizCacheManager_10M";

     /**
      * 前端业务缓存管理器
      *
      * @return {@link CacheManager }
      */
     @Bean(name = CACHE_MANAGER_1M)
    public CacheManager frontBizCacheManager1M() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

     /**
      * 前台业务缓存管理
      *
      * @return {@link CacheManager }
      */
     @Bean(name = CACHE_MANAGER_5M)
    public CacheManager frontBizCacheManager5M() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

     /**
      * 前端业务缓存管理器
      *
      * @return {@link CacheManager }
      */
     @Bean(name = CACHE_MANAGER_10M)
    public CacheManager frontBizCacheManager10M() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

}
