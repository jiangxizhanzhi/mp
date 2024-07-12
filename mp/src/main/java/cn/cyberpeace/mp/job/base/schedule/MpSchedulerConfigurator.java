 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MpSchedulerConfigurator.java.
 // File size 945.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.job.base.schedule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName MpSchedulerConfigurator
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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
