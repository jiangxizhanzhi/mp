package cn.cyberpeace.mp.devops.cluster.init;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.InitRegisterService;
import cn.cyberpeace.mp.devops.cluster.config.IpAddressConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServerConfigListenerRunner implements ApplicationRunner {

    @Autowired
    private InitRegisterService initRegisterService;

    @Autowired
    private IpAddressConfig ipAddressConfig;

    public String getUrl() {
        return "http://" + ipAddressConfig.getMp();
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            initRegisterService.register(getUrl());
        }catch (Exception e){
            log.error("ServerConfigListener onApplicationEvent error : {} ",e);
        }
    }
}