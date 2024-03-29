package cn.cyberpeace.mp.base.cron;

import cn.cyberpeace.mp.base.annotation.LocalCallFunc;
import cn.cyberpeace.mp.base.annotation.LocalCallServer;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;

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
            String detail = "load cron job error , cron job " + taskInfo.getTaskId() + " exists ";
            log.error(detail);
            throw new MpRuntimeException(BaseErrorCode.ERROR, detail);
        } else {
            TASK_INFOS.put(taskInfo.getTaskId(), taskInfo);
            if (taskInfo.getInitialDelaySeconds() > 0) {
                // 在初始化后需要延迟执行的
                var loadJobTime = new Date(System.currentTimeMillis() + taskInfo.getInitialDelaySeconds() * 1000);
                taskScheduler.schedule(() -> {
                    this.doLoadJob(taskInfo);
                }, loadJobTime);
            } else {
                // 可以直接执行的
                this.doLoadJob(taskInfo);
            }
        }
    }

    private void doLoadJob(TaskInfo taskInfo) {
        ScheduledFuture<?> s = taskScheduler.schedule(new FormatJob(taskInfo), new CronTrigger(taskInfo.getCron()));
        CRON_JOBS.put(taskInfo.getTaskId(), s);
        log.info("load cron job , task info is [{}]", taskInfo.getShowInfo());
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
            CRON_JOBS.remove(id)
                    .cancel(false);
            // 开启新任务
            this.doLoadJob(taskInfo);
            log.info("update cron job , new info is [{}]", taskInfo.getShowInfo());
        }
    }

    private static class FormatJob implements Runnable {
        private ReentrantLock lock;

        private TaskInfo taskInfo;

        public FormatJob(TaskInfo taskInfo) {
            this.taskInfo = taskInfo;
            this.lock = new ReentrantLock();
        }

        @Override
        public void run() {
            if (this.lock.tryLock()) {
                try {
                    log.debug("start job --> " + taskInfo.getShowInfo());
                    taskInfo.getMethodTask()
                            .action();
                    log.debug("end job --> " + taskInfo.getShowInfo());
                } finally {
                    this.lock.unlock();
                }
            } else {
                log.info("cron job is running , job info is [{}]", taskInfo.getShowInfo());
            }
        }
    }
}