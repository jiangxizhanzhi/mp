package cn.cyberpeace.mp.aggregation.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 模块下定义的独立配置信息
 * 约定：各模块的独立配置命名 {模块名}AppConfig
 */
@Configuration
@ConfigurationProperties(prefix = "mp.aggregation")
@Data
public class AggregationConfig {

    @Value("${mp.aggregation.static-locations}")
    private String staticLocation;

}
