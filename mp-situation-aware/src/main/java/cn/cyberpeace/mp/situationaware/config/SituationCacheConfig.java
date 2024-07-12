 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SituationCacheConfig.java.
 // File size 958.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/8 10:33
 */
@Configuration
public class SituationCacheConfig {

    public static final String CACHE_MANAGER = "situationCacheManager";

    @Bean(name = CACHE_MANAGER)
    public CacheManager situationCacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(20, TimeUnit.SECONDS).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

}
