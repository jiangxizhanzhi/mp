package cn.cyberpeace.mp.base.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 模块下定义的独立配置信息
 * 约定：各模块的独立配置命名 {模块名}AppConfig
 * 如：base模块下为 BaseAppConfig
 */
@Configuration
@ConfigurationProperties(prefix = "mp.base")
@Data
public class BaseAppConfig {

    @Value("${mp.base.default-page-size:10}")
    private Integer defaultPageSize;

    @Value("${mp.base.max-page-size:1000}")
    private Integer maxPageSize;

    @Value("${mp.base.date-time-pattern:yyyy-MM-dd HH:mm:ss}")
    private String dateTimePattern;

    @Value("${mp.base.date-pattern:yyyy-MM-dd}")
    private String datePattern;

    @Value("${mp.base.time-pattern:HH:mm:ss}")
    private String timePattern;

}
