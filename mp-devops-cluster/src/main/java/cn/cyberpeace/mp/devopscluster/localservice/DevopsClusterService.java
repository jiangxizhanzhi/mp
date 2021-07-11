package cn.cyberpeace.mp.devopscluster.localservice;

import cn.cyberpeace.mp.base.annotation.LocalCallFunc;
import cn.cyberpeace.mp.base.annotation.LocalCallServer;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.InitRegisterService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
@LocalCallServer
public class DevopsClusterService {
    @Resource
    private InitRegisterService initRegisterService;

    
    @LocalCallFunc
    public void initTarget() {
        initRegisterService.initTarget();
    }
}
