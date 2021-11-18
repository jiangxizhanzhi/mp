package cn.cyberpeace.mp.base.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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

    @Value("${mp.base.scheduled-thread-pool-core-size:50}")
    private Integer scheduledThreadPoolCoreSize;

    @Value("${mp.base.json-stringify-include-null:true}")
    private Boolean jsonStringifyIncludeNull;

    @Value("${mp.base.task-pool-size:1000}")
    private Integer taskPoolSize;

    @Value("${mp.base.task-hold-size:200000}")
    private Integer taskHoldSize;

    @Value("${mp.base.fdfs-tracker-servers:127.0.0.1:11211}")
    private List<String> fdfsTrackerServers;

    @Value("${mp.base.fdfs-inner-http-server:127.0.0.1:8888}")
    private String fdfsInnerHttpServer;

    @Value("${mp.base.fdfs-outer-http-server:127.0.0.1:8888}")
    private String fdfsOuterHttpServer;

}
