 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneCacheConfig.java.
 // File size 1129.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName CsceneCacheConfig
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/4/7 20:04
 * @Version 1.0.0
 */
@Configuration
public class CsceneCacheConfig {

    public static final String CACHE_MANAGER = "csceneCacheManager";

    /**
     * csceneCacheManager
     *
     * @return
     */
    @Bean(name = CACHE_MANAGER)
    @Primary
    public CacheManager csceneCacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(20, TimeUnit.SECONDS).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

}
