package cn.cyberpeace.mp.devops.cluster.init;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.InitRegisterService;
import cn.cyberpeace.mp.devops.cluster.config.IpAddressConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServerConfigListenerRunner implements ApplicationRunner {

    @Autowired
    private InitRegisterService initRegisterService;

    @Autowired
    private IpAddressConfig ipAddressConfig;

    @Autowired
    private Environment environment;

    private String serverPort;

    public String getUrl() {
        return "http://" + ipAddressConfig.getMp() + ":" + this.serverPort;
    }

    @Override
    public void run(ApplicationArguments args) {
        this.serverPort = environment.getProperty("server.port", "8080");
        try {
            initRegisterService.register(getUrl());
        }catch (Exception e){
            log.error("ServerConfigListener onApplicationEvent error : {} ",e);
        }
    }
}