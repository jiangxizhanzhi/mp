 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserOnlineRecordScheduleService.java.
 // File size 4866.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
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

 /**
  * 用户在线记录时间表服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Service
@Slf4j
public class UserOnlineRecordScheduleService {

     /**
      * 刷新率
      */// 每2分钟统计一次
    public static final long REFRESH_RATE = 2;

     /**
      *
      */// 两分钟内，心跳次数大于1
    public static final int HEARTBEAT_COUNT_THRESHOLD = 1;

     /**
      *
      */
     public static final int BATCH_TASK_USER_SIZE = 10;

     /**
      *
      */
     private static ExecutorService threadPool = null;

     /**
      * 心跳服务
      */
     @Autowired
    private UserHeartbeatService heartbeatService;

     /**
      * 创建或者更新用户在线记录
      */
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
                // 一段时间内，心跳次数超过阈值，更新在线记录表
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

     /**
      * 检查状态是否过期，过期状态更新
      */
     @Scheduled(initialDelay = 1, fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void checkUserOlRecord() {
        if (threadPool == null) {
            threadPool = getSingletonThreadPool();
        }
        UserOnlineRecordStatusCheckTask recordStatusCheckTask = new UserOnlineRecordStatusCheckTask();
        threadPool.submit(recordStatusCheckTask);
    }

     /**
      * 静态内部类的单例模式
      *
      * @return
      */
     public static ExecutorService getSingletonThreadPool() {
        return ThreadPoolHolder.threadPoolExecutor;
    }

     /**
      * @author 二竹
      * @date 2025/01/17
      */
     private static class ThreadPoolHolder {
         /**
          * 命名螺纹厂
          */
         static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("online-report-pool-%d").build();
         /**
          * 线程池执行程序
          */
         private static ExecutorService threadPoolExecutor = new ThreadPoolExecutor(5, 100, 30, TimeUnit.SECONDS,
                new ArrayBlockingQueue(10000), namedThreadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
    }

}
