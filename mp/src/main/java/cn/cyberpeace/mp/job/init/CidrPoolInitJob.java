 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CidrPoolInitJob.java.
 // File size 656.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.job.base.init.MpInitiate;
import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName CidrPoolInitJob
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Component
public class CidrPoolInitJob {

    @Resource
    private CloudAppConfig cloudAppConfig;

    @MpInitiate(description = "初始化cidr池子", id = "cidr_pool_init")
    public void run() {
        cloudAppConfig.init();
    }

}
