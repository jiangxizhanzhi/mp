package cn.cyberpeace.mp.front.admin.service;

import cn.cyberpeace.mp.base.annotation.LocalCallFunc;
import cn.cyberpeace.mp.base.annotation.LocalCallServer;
import cn.cyberpeace.mp.front.admin.dto.activation.ActivationResponse;
import cn.cyberpeace.mp.front.admin.service.sys.SysActivationService;
import cn.cyberpeace.mp.front.admin.service.sys.activmenu.ActivMenuService;
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

    /**
     * 根据激活项重置菜单visible属性
     */
    @LocalCallFunc
    public void resetMenus() {
        log.info("local-call resetMenus");
        ActivationResponse activationResponse = sysActivationService.status();
        activMenuService.resetMenusVisibility(activationResponse);
        log.info("local-call resetMenus success!");
    }
}
