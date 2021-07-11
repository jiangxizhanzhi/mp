package cn.cyberpeace.mp.base.service.impl;

import cn.cyberpeace.mp.base.service.ISystemStatusInfo;
import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.base.util.thread.taskpool.ThreadPoolInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;


@Slf4j
@Lazy
@Service
public class SystemStatusInfoImpl implements ISystemStatusInfo {

    public static final String SUFFIX = "-";

    @Override
    public List<ThreadPoolInfo> getThreadPoolInfos() {
        Set<TaskPool> poolSet = TaskPool.getTaskPoolInstanceCache();
        if(poolSet.isEmpty()){
            log.info("MP threadpools are empty");
            return null;
        }
        List<ThreadPoolInfo> list = poolSet.stream().map(pool -> new ThreadPoolInfo() {
            {
                int coreSize = pool.getExecutorService().getCorePoolSize();
                int currentSize = pool.getExecutorService().getPoolSize();
                int activeSize = pool.getExecutorService().getActiveCount();
                int maxSize = pool.getExecutorService().getMaximumPoolSize();
                int largestPoolSize= pool.getExecutorService().getLargestPoolSize();
                int taskQueueSize = pool.getExecutorService().getQueue().size();
                long taskCount = pool.getExecutorService().getTaskCount();
                ThreadFactory factory = pool.getExecutorService().getThreadFactory();
                String name = pool.getClass().getName();
                if (factory != null && factory instanceof CustomizableThreadFactory) {
                    name = ((CustomizableThreadFactory) factory).getThreadNamePrefix();
                    if(name.endsWith(SUFFIX)){
                        name = name.substring(0,name.length()-1);
                    }
                }
                this.setThreadPoolName(name);
                this.setCoreThreadSize(coreSize);
                this.setCurrentThreadSize(currentSize);
                this.setActiveThreadSize(activeSize);
                this.setMaxThreadSize(maxSize);
                
                this.setLargestPoolSize(largestPoolSize);
                
                this.setTaskQueueSize(taskQueueSize);
                
                this.setTaskCount(taskCount);
            }
        }).collect(Collectors.toList());
        log.info("MP threadpool information:{}", list);
        return list;
    }

    @PostConstruct
    public void print(){
        getThreadPoolInfos();
    }

}
