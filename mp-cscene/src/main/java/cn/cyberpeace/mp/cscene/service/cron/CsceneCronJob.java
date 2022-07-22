package cn.cyberpeace.mp.cscene.service.cron;

import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.service.ScenePluginCloudSceneCronService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;

import javax.annotation.Resource;

/**
 * @Description
 * @Author maojun
 * @Date 2022/7/22
 */
@Configuration
@EnableScheduling
public class CsceneCronJob implements SchedulingConfigurer {

    private static String cron;

    @Resource
    private ScenePluginCloudSceneCronService scenePluginCloudSceneCronService;

    @Autowired
    public CsceneCronJob(CsceneAppConfig appConfig) {
        cron = appConfig.getCronJobExpression();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(new TriggerTask(() -> scenePluginCloudSceneCronService.action(), triggerContext -> {
            CronTrigger cronTrigger = new CronTrigger(cron);
            return cronTrigger.nextExecutionTime(triggerContext);
        }));
    }

    public void updateCron(String cron) {
        CsceneCronJob.cron = cron;
    }
}
