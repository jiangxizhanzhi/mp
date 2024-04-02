package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.base.job.init.MpInitiate;
import cn.cyberpeace.mp.sceneplugincloud.util.SceneInstanceLinkCacheUtil;
import org.springframework.stereotype.Component;

/**
 * @Description 初始化场景链路状态的线程池和更新周期
 * @Author maojun
 * @Date 2022/8/5
 */
@Component
public class CloudInstanceLinkInitJob {
    @MpInitiate(description = "初始化节点状态链接的存储缓存", id = "scene_instance_link_cache_init")
    public void doExecute() {
        SceneInstanceLinkCacheUtil.init();
    }
}
