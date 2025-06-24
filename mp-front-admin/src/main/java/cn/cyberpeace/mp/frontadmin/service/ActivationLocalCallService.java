 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ActivationLocalCallService.java.
 // File size 1198.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
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
  * 激活当地呼叫服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Slf4j
@Service
@LocalCallServer
public class ActivationLocalCallService {

     /**
      * sys激活服务
      */
     @Autowired
    private SysActivationService sysActivationService;

     /**
      * 活性菜单服务
      */
     @Autowired
    private ActivMenuService activMenuService;

     /**
      * 根据激活项重置菜单visible属性
      */
     @LocalCallFunc
    public void resetMenus() {
        log.info("local-call resetMenus");
        ActivationResponse activationResponse = sysActivationService.status();
         final boolean b = activMenuService.resetMenusVisibility(activationResponse);
         activMenuService.resetOptionsVisibility(activationResponse);
        log.info("local-call resetMenus success!");
    }
}
