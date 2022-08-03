package cn.cyberpeace.mp.devops.cluster.localservice;

import cn.cyberpeace.mp.base.annotation.LocalCallFunc;
import cn.cyberpeace.mp.base.annotation.LocalCallServer;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.InitRegisterService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description
 * @Author hewei
 * @Date 2022/8/2
 */
@Component
@LocalCallServer
public class DevopsClusterService {
    @Resource
    private InitRegisterService initRegisterService;

    /**
     * 主动触发初始靶标同步
     */
    @LocalCallFunc
    public void initTarget() {
        initRegisterService.initTarget();
    }
}
