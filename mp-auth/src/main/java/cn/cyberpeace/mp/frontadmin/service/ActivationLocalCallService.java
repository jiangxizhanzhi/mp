 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ActivationLocalCallService.java.
 // File size 1326.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadmin.service;

import cn.cyberpeace.mp.base.annotation.LocalCallFunc;
import cn.cyberpeace.mp.base.annotation.LocalCallServer;
import cn.cyberpeace.mp.frontadmin.response.activation.ActivationResponse;
import cn.cyberpeace.mp.frontadmin.service.sys.SysActivationService;
import cn.cyberpeace.mp.frontadmin.service.sys.activmenu.ActivMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName ActivationLocalCallService
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Service
@LocalCallServer
public class ActivationLocalCallService {

    @Autowired
    private SysActivationService sysActivationService;

    @Autowired
    private ActivMenuService activMenuService;

    /**
     * 根据激活项重置菜单visible属性
     */
    @LocalCallFunc
    public void resetMenus() {
        log.info("local-call resetMenus");
        ActivationResponse activationResponse = sysActivationService.status();
        activMenuService.resetMenusVisibility(activationResponse);
        activMenuService.resetOptionsVisibility(activationResponse);
        log.info("local-call resetMenus success!");
    }
}
