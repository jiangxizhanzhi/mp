package cn.cyberpeace.mp.collectionapi.service.cron;

import cn.cyberpeace.mp.collection.core.es.CollectionClient;
import cn.cyberpeace.mp.collection.model.WazuhEventRequest;
import cn.cyberpeace.mp.collection.service.FlowInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Configuration
@EnableScheduling
@Slf4j
public class DataCleanCronJob implements SchedulingConfigurer {

    private static String cron = "0 0 0 * * ?";

    private static Integer day = 3;

    @Resource
    CollectionClient collectionClient;

    @Resource(name = "flowInfoServiceImpl")
    FlowInfoService flowInfoService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(new TriggerTask(() -> {
            try {
                // 停一秒 避免定时任务太快不停的跑
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            log.info("delete es doc start");
            var start = LocalDateTime.now().plusDays(-(day * 2));
            var end = LocalDateTime.now().plusDays(-day);
            WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
            wazuhEventRequest.setStartTime(start);
            wazuhEventRequest.setEndTime(end);
            collectionClient.deleteDocByTime(wazuhEventRequest);
            flowInfoService.deleteDoc(start, end);
        }, triggerContext -> {
            CronTrigger cronTrigger = new CronTrigger(cron);
            return cronTrigger.nextExecutionTime(triggerContext);
        }));
    }

    public void updateCron(String cron, Integer day) {
        DataCleanCronJob.cron = cron;
        DataCleanCronJob.day = day;
    }
}
