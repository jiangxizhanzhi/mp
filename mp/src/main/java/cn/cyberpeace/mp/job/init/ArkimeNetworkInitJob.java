 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ArkimeNetworkInitJob.java.
 // File size 829.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.job.base.init.MpInitiate;
import cn.cyberpeace.mp.situationaware.service.NetworkTrafficGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 启动完后生命周期进行
 */
/**
 * @ClassName ArkimeNetworkInitJob
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Component
@Slf4j
public class ArkimeNetworkInitJob {

    @Autowired
    private NetworkTrafficGroupService networkTrafficGroupService;

    @MpInitiate(description = "arkime网络信息初始化", id = "arkime_network_init")
    public void onApplicationEvent() {
        networkTrafficGroupService.initArkimeNetwork();
    }


}
