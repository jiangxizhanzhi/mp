 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AggregationConfig.java.
 // File size 811.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 模块下定义的独立配置信息
 * 约定：各模块的独立配置命名 {模块名}AppConfig
 */
/**
 * @ClassName AggregationConfig
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Configuration
@ConfigurationProperties(prefix = "spring")
@Data
public class AggregationConfig {

    @Value("${spring.web.resources.static-locations}")
    private String staticLocation;

    @Value("${mp.aggregation.export-image-timeout:3600000}")
    private Integer exportImageTimeout;

}
