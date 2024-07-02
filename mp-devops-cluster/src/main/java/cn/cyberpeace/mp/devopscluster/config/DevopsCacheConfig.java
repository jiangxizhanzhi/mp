package cn.cyberpeace.mp.devopscluster.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName DevopsCacheConfig
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/08/02 15:05
 * @Version 1.0.0
 */
@Configuration
public class DevopsCacheConfig {

    public static final String CACHE_MANAGER_20S = "devopsCacheManager_20S";


    /**
     * devopsCacheManager20S
     *
     * @return
     */
    @Bean(name = CACHE_MANAGER_20S)
    public CacheManager devopsCacheManager20S() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder().expireAfterWrite(20, TimeUnit.SECONDS).recordStats();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }


}
