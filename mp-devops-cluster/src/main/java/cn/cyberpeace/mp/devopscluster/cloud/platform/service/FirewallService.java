 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FirewallService.java.
 // File size 1094.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Firewall;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * @ClassName FirewallService
 * @Description 运维管控-系统运维-防火墙
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCpService
 */
@Service
@Slf4j
public class FirewallService extends BaseCpService {

    /**
     * FirewallService
     *
     * @param devopsClusterConfig
     */
    public FirewallService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * 分页查询防火墙
     * @param getParams
     * @return
     */
    public CloudPlatformPageResult<Firewall> page(String getParams) {
        return new CloudPlatformPageResult<>(Collections.emptyList());
    }


}
