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


@Configuration
public class AuthCacheConfig {

    
    public static final String CACHE_MANAGER_NORMAL = "authCacheManager_normal";

    
    public static final String CACHE_MANAGER_MEDIUM = "authCacheManager_medium";

    
    public static final String CACHE_MANAGER_LONG = "authCacheManager_long";

    
    public static final String CACHE_MANAGER_6H = "authCacheManager_6H";

    
    public static final String LOGIN_FAILURE = "login_failure";

    @Autowired
    private AuthConfig authConfig;


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

    @Bean(name = LOGIN_FAILURE)
    public Cache<String, Integer> loginFailureCache() {
        return Caffeine.newBuilder()
                
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
    }

}
