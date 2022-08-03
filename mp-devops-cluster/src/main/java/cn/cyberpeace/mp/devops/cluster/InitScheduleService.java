package cn.cyberpeace.mp.devops.cluster;

import cn.cyberpeace.mp.devops.cluster.init.ServerConfigListenerRunner;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

public class InitScheduleService {
    // 每5分钟执行一次
    public static final long REFRESH_RATE = 5;

    private static ServerConfigListenerRunner devopsInit = new ServerConfigListenerRunner();


    @Scheduled(initialDelay = REFRESH_RATE, fixedRate = REFRESH_RATE, timeUnit = TimeUnit.MINUTES)
    public void initSchedule() {
        devopsInit.init();
    }
}
