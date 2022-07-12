package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.user.UserOnlineRecordStatusCheckTask;
import cn.cyberpeace.mp.auth.dto.user.UserOnlineRecordTask;
import cn.cyberpeace.mp.auth.model.UserModel;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
@Slf4j
public class UserOnlineRecordScheduleService {

    // 每2分钟统计一次
    public static final long REFRESH_RATE = 2;

    public static final long REFRESH_MINUTE = 60000;

    //    // 每次间隔的次数(每分钟一次，15分钟就是15次，80% 就是12次）
//    public static final double HEARTBEAT_PER_RATE = 0.8 * 15;
    public static final int HEARTBEAT_COUNT_THRESHOLD = 1;

    private static ExecutorService threadPool = null;

    public static int REALTIME_ONLINE_USER_CNT = 0;

    @Autowired
    private UserHeartbeatService heartbeatService;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 创建或者更新用户在线记录
     */
    @Scheduled(initialDelay = REFRESH_RATE * REFRESH_MINUTE, fixedRate = REFRESH_RATE * REFRESH_MINUTE)
    public void scheduleUserOlRecord() {
        if (threadPool == null) {
            threadPool = getSingletonThreadPool();
        }
        log.info("start online record task...");
        ConcurrentHashMap<UserModel, CopyOnWriteArrayList<LocalDateTime>> heartbeatMap = heartbeatService.getHeartbeatMap();
        REALTIME_ONLINE_USER_CNT = heartbeatMap.size();
        log.info("REALTIME_ONLINE_USER_CNT:{}", REALTIME_ONLINE_USER_CNT);
        for (Map.Entry<UserModel, CopyOnWriteArrayList<LocalDateTime>> entry : heartbeatMap.entrySet()) {
            UserModel userModel = entry.getKey();
            String userId = userModel.getId();
            CopyOnWriteArrayList<LocalDateTime> timestampList = entry.getValue();
            if (timestampList.size() >= HEARTBEAT_COUNT_THRESHOLD) {
                // 一段时间内，心跳次数超过阈值，更新在线记录表
                UserOnlineRecordTask recordTask = new UserOnlineRecordTask(userId, timestampList, applicationContext);
                threadPool.submit(recordTask);
                heartbeatMap.remove(userModel);
            }
        }

        log.info("finish online record task...");
        log.info("heartbeatMap.size:{} after...", heartbeatMap.size());

    }


    /**
     * 检查状态是否过期，过期状态更新
     */
    @Scheduled(initialDelay = 10000, fixedRate = 5 * REFRESH_MINUTE)
    public void checkUserOlRecord() {
        if (threadPool == null) {
            threadPool = getSingletonThreadPool();
        }
        log.info("start online record status check task...");

        UserOnlineRecordStatusCheckTask recordStatusCheckTask = new UserOnlineRecordStatusCheckTask(applicationContext);
        threadPool.submit(recordStatusCheckTask);
        log.info("finish online record status check task...");
    }

    /**
     * 静态内部类的单例模式
     *
     * @return
     */
    public static ExecutorService getSingletonThreadPool() {
        return ThreadPoolHolder.threadPoolExecutor;
    }

    private static class ThreadPoolHolder {
        static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("online-report-pool-%d").build();
        private static ExecutorService threadPoolExecutor = new ThreadPoolExecutor(30, 200, 180, TimeUnit.SECONDS,
                new ArrayBlockingQueue(10000), namedThreadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
    }

}
