 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowAppConfig.java.
 // File size 2245.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scenepluginflow.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "mp.scenepluginflow")
@Data
/**
 * 流程应用配置类，用于存储和管理流程应用的相关配置信息。
 * 通过@Value注解从应用的配置文件中动态获取配置值，提供灵活性和可配置性。
 */
public class FlowAppConfig {

    /**
     * 创建操作的线程池大小。
     * 通过@Value注解从配置文件中获取mp.scenepluginflow.create_pool_size的值，如果未配置则默认为10。
     * 这个配置项用于控制创建操作时使用的线程池大小，以平衡资源使用和性能。
     */
    @Value("${mp.scenepluginflow.create_pool_size:10}")
    private Integer createPoolSize;

    /**
     * 创建操作的队列容量。
     * 通过@Value注解从配置文件中获取mp.scenepluginflow.create_hold_size的值，如果未配置则默认为200000。
     * 这个配置项用于控制创建操作的队列容量，以防止过度的创建请求导致系统负担过重。
     */
    @Value("${mp.scenepluginflow.create_hold_size:200000}")
    private Integer createHoldSize;

    /**
     * 删除操作的线程池大小。
     * 通过@Value注解从配置文件中获取mp.scenepluginflow.delete_pool_size的值，如果未配置则默认为10。
     * 这个配置项用于控制删除操作时使用的线程池大小，以平衡资源使用和性能。
     */
    @Value("${mp.scenepluginflow.delete_pool_size:10}")
    private Integer deletePoolSize;

    /**
     * 删除操作的队列容量。
     * 通过@Value注解从配置文件中获取mp.scenepluginflow.delete_hold_size的值，如果未配置则默认为200000。
     * 这个配置项用于控制删除操作的队列容量，以防止过度的删除请求导致系统负担过重。
     */
    @Value("${mp.scenepluginflow.delete_hold_size:200000}")
    private Integer deleteHoldSize;

}
