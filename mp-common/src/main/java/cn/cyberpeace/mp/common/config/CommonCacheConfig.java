package cn.cyberpeace.mp.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;


@Configuration
public class CommonCacheConfig {
    public static final String CM_300S = "commonCacheManager_300s";

    @Bean(name = CM_300S)
    public CacheManager commonCacheManager300s() {
        var caffeineCacheManager = new CaffeineCacheManager();
        var caffeine = Caffeine.newBuilder().expireAfterWrite(300, TimeUnit.SECONDS).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

}
