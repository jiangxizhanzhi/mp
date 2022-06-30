package cn.cyberpeace.mp.devops.log.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author：Geek Wang
 * @date: 2022/6/30
 */
@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "mp.log.async-thread-pool",ignoreInvalidFields = true)
public class LogAsyncThreadPoolConfig {

    private int corePoolSize;

    private int maxPoolSize;

    private int queueCapacity;

    private int keepAliveSeconds;

    private int awaitTerminationSeconds;

    private String threadNamePrefix;
}
