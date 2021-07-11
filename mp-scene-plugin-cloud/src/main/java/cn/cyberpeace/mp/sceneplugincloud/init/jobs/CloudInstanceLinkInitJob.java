package cn.cyberpeace.mp.sceneplugincloud.init.jobs;

import cn.cyberpeace.mp.sceneplugincloud.util.SceneInstanceLinkCacheUtil;
import org.springframework.stereotype.Component;


@Component
public class CloudInstanceLinkInitJob extends AbstractInitJob {
    @Override
    protected boolean doExecute() {
        SceneInstanceLinkCacheUtil.init();
        return true;
    }

    @Override
    protected boolean getSupportRefresh() {
        return false;
    }
}
