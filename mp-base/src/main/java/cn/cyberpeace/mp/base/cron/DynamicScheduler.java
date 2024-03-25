package cn.cyberpeace.mp.base.cron;

import cn.cyberpeace.mp.base.annotation.LocalCallFunc;
import cn.cyberpeace.mp.base.annotation.LocalCallServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Component
@Slf4j
@LocalCallServer
public class DynamicScheduler {

    private static Map<String, TaskInfo> TASK_INFOS = new ConcurrentHashMap<>();
    private static Map<String, ScheduledFuture<?>> CRON_JOBS = new ConcurrentHashMap<>();

    @Resource
    private TaskScheduler taskScheduler;

    public void startJob(TaskInfo taskInfo) {
        if (TASK_INFOS.containsKey(taskInfo.getTaskId())) {
            log.error("start cron job error , cron job {} exists ", taskInfo.getTaskId());
        } else {
            TASK_INFOS.put(taskInfo.getTaskId(), taskInfo);
            ScheduledFuture<?> s = taskScheduler.schedule(taskInfo.getJob(), new CronTrigger(taskInfo.getCron()));
            CRON_JOBS.put(taskInfo.getTaskId(), s);
            log.info("start cron job , task info is [{}]", taskInfo.getShowInfo());
        }
    }

    @LocalCallFunc
    public void updateJob(String id, String cron) {
        log.info("update cron job , input params is {} ,{}", id, cron);
        TaskInfo taskInfo = TASK_INFOS.remove(id);
        if (taskInfo == null) {
            log.error("update cron job error , cron job {} not exists ", id);
        } else {
            log.info("update cron job , origin info is [{}]", taskInfo.getShowInfo());
            taskInfo.setCron(cron);
            // 停止原任务
            CRON_JOBS
                    .remove(id)
                    .cancel(true);
            // 开启新任务
            this.startJob(taskInfo);
            log.info("update cron job , new info is [{}]", taskInfo.getShowInfo());
        }
    }

}