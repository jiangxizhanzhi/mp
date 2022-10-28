package cn.cyberpeace.mp.devopscluster.init;

import cn.cyberpeace.mp.devopscluster.cloud.platform.service.InitRegisterService;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
    private DevopsClusterConfig devopsClusterConfig;


    public String getUrl() {
        return "http://" + devopsClusterConfig.getMpIp() + ":" + devopsClusterConfig.getMpPort();
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            initRegisterService.register(getUrl());
            initRegisterService.initTarget();
        } catch (Exception e) {
            log.error("ServerConfigListener onApplicationEvent error : {} ", ExceptionUtils.getStackTrace(e));
        }
    }
}
