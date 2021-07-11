package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.service.task.UserOnlineRecordStatusCheckTask;
import cn.cyberpeace.mp.auth.service.task.UserOnlineRecordTask;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.hutool.core.collection.CollectionUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class UserOnlineRecordScheduleService {

    
    public static final long REFRESH_RATE = 2;

    
    public static final int HEARTBEAT_COUNT_THRESHOLD = 1;

    public static final int BATCH_TASK_USER_SIZE = 10;

    private static ExecutorService threadPool = null;

    @Autowired
    private UserHeartbeatService heartbeatService;

    
    @Scheduled(initialDelay = REFRESH_RATE, fixedRate = REFRESH_RATE, timeUnit = TimeUnit.MINUTES)
    public void scheduleUserOlRecord() {
        LocalDateTime begin = LocalDateTime.now();
        if (threadPool == null) {
            threadPool = getSingletonThreadPool();
        }
        ConcurrentHashMap<UserModel, CopyOnWriteArrayList<LocalDateTime>> heartbeatMap = heartbeatService.getHeartbeatMap();
        List<Map<UserModel, LocalDateTime>> userModel2LastTsMapList = new ArrayList<>();
        int i = 0;
        Map<UserModel, LocalDateTime> map = null;
        List<UserModel> removeList = new ArrayList<>();
        for (Map.Entry<UserModel, CopyOnWriteArrayList<LocalDateTime>> entry : heartbeatMap.entrySet()) {
            UserModel userModel = entry.getKey();
            CopyOnWriteArrayList<LocalDateTime> timestampList = entry.getValue();
            if (timestampList.size() >= HEARTBEAT_COUNT_THRESHOLD) {
                
                if (i % BATCH_TASK_USER_SIZE == 0) {
                    map = new HashMap<>();
                    userModel2LastTsMapList.add(map);
                }
                map.put(userModel, timestampList.get(0));
                removeList.add(userModel);
                i++;
            }
        }

        if (CollectionUtil.isNotEmpty(userModel2LastTsMapList)) {
            for (Map<UserModel, LocalDateTime> userModel2LastTsMap : userModel2LastTsMapList) {
                UserOnlineRecordTask recordTask = new UserOnlineRecordTask(userModel2LastTsMap);
                threadPool.submit(recordTask);
            }
        }

        for (UserModel userModel : removeList) {
            CopyOnWriteArrayList<LocalDateTime> beatList = heartbeatMap.get(userModel);
            if (beatList.stream().anyMatch(t -> t.isAfter(begin))) {
                CopyOnWriteArrayList timestampList = new CopyOnWriteArrayList();
                timestampList.add(begin);
                heartbeatMap.put(userModel, timestampList);
            } else {
                heartbeatMap.remove(userModel);
            }
        }
    }


    
    @Scheduled(initialDelay = 1, fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void checkUserOlRecord() {
        if (threadPool == null) {
            threadPool = getSingletonThreadPool();
        }
        UserOnlineRecordStatusCheckTask recordStatusCheckTask = new UserOnlineRecordStatusCheckTask();
        threadPool.submit(recordStatusCheckTask);
    }

    
    public static ExecutorService getSingletonThreadPool() {
        return ThreadPoolHolder.threadPoolExecutor;
    }

    private static class ThreadPoolHolder {
        static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("online-report-pool-%d").build();
        private static ExecutorService threadPoolExecutor = new ThreadPoolExecutor(5, 100, 30, TimeUnit.SECONDS,
                new ArrayBlockingQueue(10000), namedThreadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
    }

}
