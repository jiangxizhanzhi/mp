package cn.cyberpeace.mp.base.config;

import cn.hutool.core.io.FileUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
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

    @Value("${mp.base.project-name:mp}")
    private String projectName;

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

    @Value("${mp.base.home-dir:}")
    private String homeDir;

    @Value("${mp.base.resources-dir:}")
    private String resourcesDir;

    @Value("${mp.base.run-init:false}")
    private boolean runInit;

    @Value("${mp.base.swagger-doc-title:mp swagger doc}")
    private String swaggerDocTitle;

    @Value("${mp.base.swagger-doc-description:mp swagger doc}")
    private String swaggerDocDescription;

    @Value("${mp.base.swagger-doc-version:1.0}")
    private String swaggerDocVersion;

    @Value("${mp.base.swagger-doc-url:}")
    private String swaggerDocUrl;

    public String getHomeDir() {
        if (!StringUtils.isEmpty(homeDir)) {
            return homeDir;
        }

        homeDir = StringUtils.joinWith(FileUtil.FILE_SEPARATOR, FileUtil.getUserHomePath(), "." + projectName);
        return homeDir;
    }

    public String getResourcesDir() {
        if (!StringUtils.isEmpty(resourcesDir)) {
            return resourcesDir;
        }

        resourcesDir = StringUtils.joinWith(FileUtil.FILE_SEPARATOR, getHomeDir(), "resources");
        return resourcesDir;
    }

}
