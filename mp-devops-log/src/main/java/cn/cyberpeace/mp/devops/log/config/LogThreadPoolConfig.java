package cn.cyberpeace.mp.devops.log.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @description:
 * @author：Geek Wang
 * @date: 2022/6/30
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class LogThreadPoolConfig {

    private final LogAsyncThreadPoolConfig logAsyncThreadPoolConfig;

    /**
     * 线程池配置
     * @return
     */
    @Bean(name = "logThreadPoolTaskExecutor")
    public ThreadPoolTaskExecutor logThreadPoolTaskExecutor() {
        log.info("---------- 线程池开始加载 ----------");
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        // 核心线程池大小
        threadPoolTaskExecutor.setCorePoolSize(logAsyncThreadPoolConfig.getCorePoolSize());
        // 最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(logAsyncThreadPoolConfig.getMaxPoolSize());
        // 队列容量
        threadPoolTaskExecutor.setQueueCapacity(logAsyncThreadPoolConfig.getQueueCapacity());
        // 活跃时间
        threadPoolTaskExecutor.setKeepAliveSeconds(logAsyncThreadPoolConfig.getKeepAliveSeconds());
        // 主线程等待子线程执行时间 任务的等待时间 如果超过这个时间还没有销毁就 强制销毁，以确保应用最后能够被关闭，而不是阻塞住
        threadPoolTaskExecutor.setAwaitTerminationSeconds(logAsyncThreadPoolConfig.getAwaitTerminationSeconds());
        // 线程名字前缀
        threadPoolTaskExecutor.setThreadNamePrefix(logAsyncThreadPoolConfig.getThreadNamePrefix());
        //停机策略
        //该方法用来设置 线程池关闭 的时候 等待 所有任务都完成后，再继续 销毁 其他的 Bean，这样这些异步任务的销毁就会先于数据库连接池对象的销毁。
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        //线程不够用时由调用的线程处理该任务
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化
        threadPoolTaskExecutor.initialize();
        log.info("---------- 线程池加载完成 ----------");
        return threadPoolTaskExecutor;
    }
}
