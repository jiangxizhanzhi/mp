package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.base.job.init.MpInitiate;
import cn.cyberpeace.mp.situationaware.service.NetworkTrafficGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 启动完后生命周期进行
 */
@Component
@Slf4j
public class ArkimeNetworkInit {

    @Autowired
    private NetworkTrafficGroupService networkTrafficGroupService;

    @MpInitiate(description = "arkime网络信息初始化", id = "arkime_network_init")
    public void onApplicationEvent() {
        networkTrafficGroupService.initArkimeNetwork();
    }


}
