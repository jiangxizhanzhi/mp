package cn.cyberpeace.mp.frontadmin.service;

import cn.cyberpeace.mp.base.annotation.LocalCallFunc;
import cn.cyberpeace.mp.base.annotation.LocalCallServer;
import cn.cyberpeace.mp.frontadmin.response.activation.ActivationResponse;
import cn.cyberpeace.mp.frontadmin.service.sys.SysActivationService;
import cn.cyberpeace.mp.frontadmin.service.sys.activmenu.ActivMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@LocalCallServer
public class ActivationLocalCallService {

    @Autowired
    private SysActivationService sysActivationService;

    @Autowired
    private ActivMenuService activMenuService;

    
    @LocalCallFunc
    public void resetMenus() {
        log.info("local-call resetMenus");
        ActivationResponse activationResponse = sysActivationService.status();
        activMenuService.resetMenusVisibility(activationResponse);
        activMenuService.resetOptionsVisibility(activationResponse);
        log.info("local-call resetMenus success!");
    }
}
