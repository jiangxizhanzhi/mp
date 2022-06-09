package cn.cyberpeace.mp.cscene.localservice;

import cn.cyberpeace.mp.base.annotation.LocalCallFunc;
import cn.cyberpeace.mp.base.annotation.LocalCallServer;
import cn.cyberpeace.mp.cscene.service.ScenePluginCloudSceneCronJob;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description
 * @Author maojun
 * @Date 2022/6/8
 */
@Component
@LocalCallServer
public class ScenePluginCloudSceneLocalService {
    @Resource
    private ScenePluginCloudSceneCronJob cloudSceneCronJob;

    /**
     * 主动触发回收异常资源的任务
     */
    @LocalCallFunc
    public void reclaimAbnormalResources() {
        cloudSceneCronJob.scheduleNode();
    }
}
