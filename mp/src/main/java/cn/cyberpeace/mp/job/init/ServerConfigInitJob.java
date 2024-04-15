package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.job.base.init.MpInitiate;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.InitRegisterService;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServerConfigInitJob {

    @Autowired
    private InitRegisterService initRegisterService;

    @Autowired
    private DevopsClusterConfig devopsClusterConfig;


    public String getUrl() {
        return "http://" + devopsClusterConfig.getMpIp() + ":" + devopsClusterConfig.getMpPort();
    }

    @MpInitiate(description = "根据底层数据同步靶标列表", id = "sync_device_list")
    public void action() {
        try {
            initRegisterService.register(getUrl());
            initRegisterService.initTarget();
        } catch (Exception e) {
            log.error("ServerConfigListener onApplicationEvent error : {} ", ExceptionUtils.getStackTrace(e));
        }
    }
}
