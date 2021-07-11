package cn.cyberpeace.mp.cscene.service.cron;

import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.service.ScenePluginCloudSceneCronService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;

import javax.annotation.Resource;


@Slf4j
@Configuration
@EnableScheduling
public class CsceneCronJob implements SchedulingConfigurer {

    private static String cron;

    @Resource
    private CsceneAppConfig csceneAppConfig;

    @Resource
    private ScenePluginCloudSceneCronService scenePluginCloudSceneCronService;

    @Autowired
    public CsceneCronJob(CsceneAppConfig appConfig) {
        cron = appConfig.getCronJobExpression();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        if (!Boolean.TRUE.equals(csceneAppConfig.getEnableSceneCronJob())) {
            log.info("scene cron job is not enable");
            return;
        }
        taskRegistrar.addTriggerTask(new TriggerTask(() -> {
            try {
                
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            scenePluginCloudSceneCronService.action();
        }, triggerContext -> {
            CronTrigger cronTrigger = new CronTrigger(cron);
            return cronTrigger.nextExecutionTime(triggerContext);
        }));
    }

    public void updateCron(String cron) {
        CsceneCronJob.cron = cron;
    }
}
