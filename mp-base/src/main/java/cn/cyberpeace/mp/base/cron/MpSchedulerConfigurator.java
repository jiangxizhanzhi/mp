package cn.cyberpeace.mp.base.cron;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class MpSchedulerConfigurator {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(15); // 设置线程池大小
        scheduler.setThreadNamePrefix("mp-scheduled-task-");
        scheduler.initialize();
        return scheduler;
    }
}
