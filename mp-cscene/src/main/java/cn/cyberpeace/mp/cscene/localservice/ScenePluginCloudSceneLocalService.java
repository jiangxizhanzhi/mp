package cn.cyberpeace.mp.cscene.localservice;

import cn.cyberpeace.mp.base.annotation.LocalCallFunc;
import cn.cyberpeace.mp.base.annotation.LocalCallServer;
import cn.cyberpeace.mp.cscene.service.ScenePluginCloudSceneCronService;
import cn.cyberpeace.mp.cscene.service.cron.CsceneCronJob;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
@LocalCallServer
public class ScenePluginCloudSceneLocalService {
    @Resource
    private ScenePluginCloudSceneCronService cloudSceneCronJob;

    @Resource
    private CsceneCronJob csceneCronJob;

    
    @LocalCallFunc
    public void reclaimAbnormalResources() {
        cloudSceneCronJob.action();
    }

    
    @LocalCallFunc
    public void setCronJobExpression(String cron) {
        csceneCronJob.updateCron(cron);
    }
}
