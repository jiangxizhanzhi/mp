 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DevopsClusterService.java.
 // File size 761.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.localservice;

import cn.cyberpeace.mp.base.annotation.LocalCallFunc;
import cn.cyberpeace.mp.base.annotation.LocalCallServer;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.InitRegisterService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName DevopsClusterService
 * @Description
 * @Author hewei
 * @Date 2022/8/2
 * @Version 1.0.0
 */
@Component
@LocalCallServer
public class DevopsClusterService {
    @Resource
    private InitRegisterService initRegisterService;

    /**
     * 主动触发初始靶标同步
     */
    @LocalCallFunc
    public void initTarget() {
        initRegisterService.initTarget();
    }
}
