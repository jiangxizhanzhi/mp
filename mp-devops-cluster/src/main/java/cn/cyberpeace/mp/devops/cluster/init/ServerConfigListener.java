package cn.cyberpeace.mp.devops.cluster.init;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.InitRegisterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@Component
public class ServerConfigListener implements ApplicationListener<WebServerInitializedEvent> {

    @Autowired
    private InitRegisterService initRegisterService;

    private int serverPort;

    public String getUrl() {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            log.error("getLocalHost error : {} ",e);
        }
        return "http://" + address.getHostAddress() + ":" + this.serverPort;
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