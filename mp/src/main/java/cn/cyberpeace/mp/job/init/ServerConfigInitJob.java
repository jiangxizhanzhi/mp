 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ServerConfigInitJob.java.
 // File size 1317.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.job.base.init.MpInitiate;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.InitRegisterService;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName ServerConfigInitJob
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Component
public class ServerConfigInitJob {

    @Autowired
    private InitRegisterService initRegisterService;

    @Autowired
    private DevopsClusterConfig devopsClusterConfig;


    public String getUrl() {
        return "http://" + devopsClusterConfig.getMpIp() + ":" + devopsClusterConfig.getMpPort();
    }

    @MpInitiate(description = "根据底层数据同步靶标列表", id = "sync_device_list")
    public void action() {
        try {
            initRegisterService.register(getUrl());
            initRegisterService.initTarget();
        } catch (Exception e) {
            log.error("ServerConfigListener onApplicationEvent error : {} ", ExceptionUtils.getStackTrace(e));
        }
    }
}
