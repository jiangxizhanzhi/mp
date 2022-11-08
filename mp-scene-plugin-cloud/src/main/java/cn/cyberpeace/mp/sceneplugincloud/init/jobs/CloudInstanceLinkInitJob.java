package cn.cyberpeace.mp.sceneplugincloud.init.jobs;

import cn.cyberpeace.mp.sceneplugincloud.util.SceneInstanceLinkCacheUtil;
import org.springframework.stereotype.Component;

/**
 * @Description 初始化场景链路状态的线程池和更新周期
 * @Author maojun
 * @Date 2022/8/5
 */
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
