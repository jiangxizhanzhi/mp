package cn.cyberpeace.mp.job.base.schedule;

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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Slf4j
@LocalCallServer
public class ScheduleJobLoader {

    private static Map<String, MpScheduleJobInfo> TASK_INFOS = new ConcurrentHashMap<>();
    private static Map<String, ScheduledFuture<?>> CRON_JOBS = new ConcurrentHashMap<>();

    @Resource
    private TaskScheduler taskScheduler;

    public void startJobs(List<MpScheduleJobInfo> jobInfos) {
        for (MpScheduleJobInfo mpScheduleJobInfo : jobInfos) {
            this.startJob(mpScheduleJobInfo);
        }
    }

    public void startJob(MpScheduleJobInfo mpScheduleJobInfo) {
        if (TASK_INFOS.containsKey(mpScheduleJobInfo.getJobId())) {
            String detail = "load cron job error , cron job " + mpScheduleJobInfo.getJobId() + " exists ";
            log.error(detail);
            throw new MpRuntimeException(BaseErrorCode.ERROR, detail);
        } else {
            TASK_INFOS.put(mpScheduleJobInfo.getJobId(), mpScheduleJobInfo);
            if (mpScheduleJobInfo.getInitialDelaySeconds() > 0) {
                // 在初始化后需要延迟执行的
                var loadJobTime = new Date(System.currentTimeMillis() + mpScheduleJobInfo.getInitialDelaySeconds() * 1000);
                taskScheduler.schedule(() -> {
                    this.doLoadJob(mpScheduleJobInfo);
                }, loadJobTime);
            } else {
                // 可以直接执行的
                this.doLoadJob(mpScheduleJobInfo);
            }
        }
    }

    private void doLoadJob(MpScheduleJobInfo mpScheduleJobInfo) {
        ScheduledFuture<?> s = taskScheduler.schedule(new FormatJob(mpScheduleJobInfo), new CronTrigger(mpScheduleJobInfo.getCron()));
        CRON_JOBS.put(mpScheduleJobInfo.getJobId(), s);
        log.info("load cron job , task info is [{}]", mpScheduleJobInfo.getShowInfo());
    }

    @LocalCallFunc
    public void updateJob(String id, String cron) {
        log.info("update cron job , input params is {} ,{}", id, cron);
        MpScheduleJobInfo mpScheduleJobInfo = TASK_INFOS.remove(id);
        if (mpScheduleJobInfo == null) {
            log.error("update cron job error , cron job {} not exists ", id);
        } else {
            log.info("update cron job , origin info is [{}]", mpScheduleJobInfo.getShowInfo());
            mpScheduleJobInfo.setCron(cron);
            // 停止原任务
            CRON_JOBS.remove(id)
                    .cancel(false);
            // 开启新任务
            this.doLoadJob(mpScheduleJobInfo);
            log.info("update cron job , new info is [{}]", mpScheduleJobInfo.getShowInfo());
        }
    }

    private static class FormatJob implements Runnable {
        private ReentrantLock lock;

        private MpScheduleJobInfo mpScheduleJobInfo;

        public FormatJob(MpScheduleJobInfo mpScheduleJobInfo) {
            this.mpScheduleJobInfo = mpScheduleJobInfo;
            this.lock = new ReentrantLock();
        }

        @Override
        public void run() {
            if (this.lock.tryLock()) {
                try {
                    log.debug("start job --> " + mpScheduleJobInfo.getShowInfo());
                    mpScheduleJobInfo.getMethodJobWrapper()
                            .action();
                    log.debug("end job --> " + mpScheduleJobInfo.getShowInfo());
                } finally {
                    this.lock.unlock();
                }
            } else {
                log.info("cron job is running , job info is [{}]", mpScheduleJobInfo.getShowInfo());
            }
        }
    }
}