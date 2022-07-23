package cn.cyberpeace.mp.cscene.init;

import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.cscene.util.NodeLimitUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.SceneInstanceLinkCacheUtil;
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
        SceneInstanceLinkCacheUtil.init();
        int count = csceneService.getActiveVirtualDeviceCount();
        NodeLimitUtils.addLimitedNodeCount(count);
    }
}
