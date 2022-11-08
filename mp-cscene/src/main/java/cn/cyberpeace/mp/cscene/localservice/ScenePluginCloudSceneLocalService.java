package cn.cyberpeace.mp.cscene.localservice;

import cn.cyberpeace.mp.base.annotation.LocalCallFunc;
import cn.cyberpeace.mp.base.annotation.LocalCallServer;
import cn.cyberpeace.mp.cscene.service.ScenePluginCloudSceneCronService;
import cn.cyberpeace.mp.cscene.service.cron.CsceneCronJob;
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
    private ScenePluginCloudSceneCronService cloudSceneCronJob;

    @Resource
    private CsceneCronJob csceneCronJob;

    /**
     * 主动触发回收异常资源的任务
     */
    @LocalCallFunc
    public void reclaimAbnormalResources() {
        cloudSceneCronJob.action();
    }

    /**
     * 更新场景回收资源定时任务的cron周期
     * @param cron
     */
    @LocalCallFunc
    public void setCronJobExpression(String cron) {
        csceneCronJob.updateCron(cron);
    }
}
