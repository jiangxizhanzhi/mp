package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.base.job.init.MpInitiate;
import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class CidrPoolInit {

    @Resource
    private CloudAppConfig cloudAppConfig;

    @MpInitiate(description = "初始化cidr池子", id = "cidr_pool_init")
    public void run() {
        cloudAppConfig.init();
    }

}
