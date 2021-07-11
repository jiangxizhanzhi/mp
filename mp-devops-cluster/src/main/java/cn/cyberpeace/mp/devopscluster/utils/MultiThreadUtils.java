package cn.cyberpeace.mp.devopscluster.utils;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopscluster.thread.HandleCallable;
import cn.cyberpeace.mp.devopscluster.thread.ITask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


@Slf4j
public class MultiThreadUtils {

    
    private int threadCount = 5;

    
    private CompletionService<ServiceResult> pool = null;

    
    public static MultiThreadUtils newInstance(int threadCount) {
        MultiThreadUtils instance = new MultiThreadUtils();
        instance.setThreadCount(threadCount);
        return instance;
    }

    
    public <T> ServiceResult execute(List<T> data, Map<String, Object> params, ITask<ServiceResult, T> task) {

        if (CollectionUtils.isEmpty(data)){
            return new ServiceResult();
        }

        
        ExecutorService threadpool = Executors.newFixedThreadPool(threadCount);

        
        pool = new ExecutorCompletionService<>(threadpool);

        
        long l = System.currentTimeMillis();

        
        int length = data.size();

        
        int taskCount = length / threadCount;

        
        for (int i = 0; i < threadCount; i++) {

            
            List<T> subData;

            if (i == (threadCount - 1)) {

                subData = data.subList(i * taskCount, length);

            } else {

                subData = data.subList(i * taskCount, (i + 1) * taskCount);

            }
            
            HandleCallable execute = new HandleCallable<T>(String.valueOf(i), subData, params, task);

            
            pool.submit(execute);
        }

        
        List<ServiceResult> result = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {

            
            ServiceResult serviceResult;
            try {
                serviceResult = pool.take().get();
                result.add(serviceResult);
            } catch (InterruptedException e) {
                log.error("error message ： {}",e.getMessage(),e);
            } catch (ExecutionException e) {
                log.error("error message ： {}",e.getMessage(),e);
            }

        }
        
        threadpool.shutdownNow();
        
        long end_l = System.currentTimeMillis();
        log.info("cost time :{} ms", (end_l - l));
        return new ServiceResult(result);
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

}
