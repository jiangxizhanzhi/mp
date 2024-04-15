package cn.cyberpeace.mp.job.base.schedule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class MpSchedulerConfigurator {

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(15); // 设置线程池大小
        scheduler.setThreadNamePrefix("mp-scheduled-task-");
        scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        scheduler.initialize();
        return scheduler;
    }
}
