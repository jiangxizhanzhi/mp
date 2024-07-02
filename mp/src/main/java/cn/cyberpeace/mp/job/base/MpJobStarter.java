package cn.cyberpeace.mp.job.base;

import cn.cyberpeace.mp.job.base.init.InitiateJobLoader;
import cn.cyberpeace.mp.job.base.init.MpInitJobInfo;
import cn.cyberpeace.mp.job.base.schedule.MpScheduleJobInfo;
import cn.cyberpeace.mp.job.base.schedule.ScheduleJobLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @ClassName MpJobStarter
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ApplicationRunner
 */
@Component
@Slf4j
public class MpJobStarter implements ApplicationRunner {

    @Resource
    private MpJobScanner mpJobScanner;

    @Resource
    private ScheduleJobLoader scheduleJobLoader;

    @Resource
    private InitiateJobLoader initiateJobLoader;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 先处理初始化任务 将任务顺序执行即可
        List<MpInitJobInfo> initJobs = mpJobScanner.getInitJobInfos();
        Collections.sort(mpJobScanner.getInitJobInfos(), Comparator.comparingInt(MpInitJobInfo::getOrder));
        this.initiateJobLoader.startJobs(initJobs);

        // 再处理定时任务 向定时任务的执行器中添加任务即可
        List<MpScheduleJobInfo> scheduleJobs = mpJobScanner.getScheduleJobInfos();
        this.scheduleJobLoader.startJobs(scheduleJobs);
    }
}
