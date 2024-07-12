 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowConfig.java.
 // File size 881.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName FlowConfig
 * @Description
 * @Author shgang
 * @Date 2022/8/31
 * @Version 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mp.flow")
public class FlowConfig {

    /**
     * trex镜像名称
     */
    @Value("${mp.flow.trex-image:tgn-trex}")
    private String trexImage;

    /**
     * 云平台地址
     */
    private String cpUrl;

    @Value("${mp.flow.trex_cpu:4}")
    private Integer trexCpu;

    @Value("${mp.flow.trex_ram:8194}")
    private Integer trexRam;

    @Value("${mp.flow.trex_disk:40}")
    private Integer trexDisk;

}
