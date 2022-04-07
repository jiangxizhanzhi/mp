package cn.cyberpeace.mp.cscene.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/7 20:04
 */
@Configuration
public class CacheConfig {

    @Bean(name = "csceneCacheManager")
    public CacheManager csceneCacheManager(Caffeine caffeine) {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

    @Bean
    public Caffeine caffeineConfig() {
        // 缓存20秒
        return Caffeine.newBuilder().expireAfterWrite(20, TimeUnit.SECONDS);
    }

}
