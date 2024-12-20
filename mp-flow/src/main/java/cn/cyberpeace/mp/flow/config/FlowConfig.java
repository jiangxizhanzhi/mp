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
    @Value("${mp.flow.cp_url:}")
    private String cpUrl;

    @Value("${mp.flow.trex_cpu:4}")
    private Integer trexCpu;

    @Value("${mp.flow.trex_ram:8192}")
    private Integer trexRam;

    @Value("${mp.flow.trex_disk:80}")
    private Integer trexDisk;

    @Value("${mp.flow.network_id:5489d262-8d06-4909-b893-4191ef036f46}")
    private String networkId;

    @Value("${mp.flow.gateway:172.30.102.1}")
    private String gateway;

    @Value("${mp.flow.cidr:5489d262-8d06-4909-b893-4191ef036f46}")
    private String cidr;

    @Value("${mp.flow.gateway:172.30.102.1}")
    private String gateway;

    @Value("${mp.flow.cscene_url:http://10.100.27.1}")
    private String csceneUrl;

    @Value("${mp.flow.trex_template_id:}")
    private String trexTemplateId;

    @Value("${mp.flow.flow_res_mode:}")
    private String flowResMode;

}
