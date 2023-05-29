package cn.cyberpeace.mp.cscene.init.jobs;

import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.cscene.util.NodeLimitUtils;
import cn.cyberpeace.mp.cscene.util.SceneLimitUtils;
import cn.cyberpeace.mp.sceneplugincloud.init.jobs.AbstractInitJob;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description 初始化场景使用的节点数量
 * @Author maojun
 * @Date 2022/8/5
 */
@Component
public class SceneNodeNumInitJob extends AbstractInitJob {

    @Resource
    private CsceneService csceneService;

    @Override
    protected boolean doExecute() {
        int count = csceneService.getActiveVirtualDeviceCount();
        NodeLimitUtils.refreshCount(count);
        var instance = csceneService.getRunningCscenes();
        SceneLimitUtils.refreshCount(instance == null ? 0 : instance.size());
        return true;
    }

    @Override
    protected boolean getSupportRefresh() {
        return false;
    }
}
