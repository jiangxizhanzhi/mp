package cn.cyberpeace.mp.devops.cluster.init;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.InitRegisterService;
import cn.cyberpeace.mp.devops.cluster.config.IpAddressConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServerConfigListener implements ApplicationListener<WebServerInitializedEvent> {

    @Autowired
    private InitRegisterService initRegisterService;

    @Autowired
    private IpAddressConfig ipAddressConfig;

    private int serverPort;

    public String getUrl() {
        return "http://" + ipAddressConfig.getMp() + ":" + this.serverPort;
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        this.serverPort = event.getWebServer().getPort();
        try {
            initRegisterService.register(getUrl());
        }catch (Exception e){
            log.error("ServerConfigListener onApplicationEvent error : {} ",e);
        }
    }

}