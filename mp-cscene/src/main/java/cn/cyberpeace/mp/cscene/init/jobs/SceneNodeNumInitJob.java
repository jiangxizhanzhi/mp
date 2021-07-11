package cn.cyberpeace.mp.cscene.init.jobs;

import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.cscene.util.NodeLimitUtils;
import cn.cyberpeace.mp.sceneplugincloud.init.jobs.AbstractInitJob;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class SceneNodeNumInitJob extends AbstractInitJob {

    @Resource
    private CsceneService csceneService;

    @Override
    protected boolean doExecute() {
        int count = csceneService.getActiveVirtualDeviceCount();
        NodeLimitUtils.refreshCount(count);
        return true;
    }

    @Override
    protected boolean getSupportRefresh() {
        return false;
    }
}
