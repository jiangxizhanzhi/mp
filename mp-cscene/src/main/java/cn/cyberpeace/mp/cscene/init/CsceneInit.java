package cn.cyberpeace.mp.cscene.init;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.cscene.util.NodeLimitUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description
 * @Author maojun
 * @Date 2022/2/11
 */
@Component
public class CsceneInit implements ApplicationRunner {

    @Resource
    private CsceneService csceneService;

    @Override
    public void run(ApplicationArguments args) {
        int count = csceneService.getActiveVirtualDeviceCount();
        NodeLimitUtils.addLimitedNodeCount(count);
    }
}
