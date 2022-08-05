package cn.cyberpeace.mp.cscene.init;

import cn.cyberpeace.mp.cscene.service.CommonConfigService;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.cscene.util.NodeLimitUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.SceneInstanceLinkCacheUtil;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudmessageconvert.CloudMessageConvertUtil;
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

    @Resource
    private CommonConfigService commonConfigService;

    @Override
    public void run(ApplicationArguments args) {

        // 初始化场景链路状态的线程池和更新周期
        SceneInstanceLinkCacheUtil.init();

        // 初始化场景使用的节点数量
        int count = csceneService.getActiveVirtualDeviceCount();
        NodeLimitUtils.addLimitedNodeCount(count);

        // 初始化云平台错误码转换的配置
        CloudMessageConvertUtil.init(commonConfigService.getCloudMessageConvert().getSource());
    }
}
