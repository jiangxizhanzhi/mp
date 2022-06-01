package cn.cyberpeace.mp.devops.cluster.utils;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.cluster.thread.HandleCallable;
import cn.cyberpeace.mp.devops.cluster.thread.ITask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 多线程工具类
 *
 */
@Slf4j
public class MultiThreadUtils {

    /**
     * 线程数量，默认5
     */
    private int threadCount = 5;

    /**
     * 线程池管理器
     */
    private CompletionService<ServiceResult> pool = null;

    /**
     * 初始化线程池和线程个数
     *
     * @param threadCount 线程数量
     * @return
     */
    public static MultiThreadUtils newInstance(int threadCount) {
        MultiThreadUtils instance = new MultiThreadUtils();
        instance.setThreadCount(threadCount);
        return instance;
    }

    /**
     * 多线程分批执行list中的任务
     *
     * @param data   具体任务数据
     * @param params 额外任务参数
     * @param task   具体任务
     * @return
     */
    public <T> ServiceResult execute(List<T> data, Map<String, Object> params, ITask<ServiceResult, T> task) {

        if (CollectionUtils.isEmpty(data)){
            return new ServiceResult();
        }

        // 创建线程池
        ExecutorService threadpool = Executors.newFixedThreadPool(threadCount);

        // 根据线程池初始化线程池管理器
        pool = new ExecutorCompletionService<>(threadpool);

        // 开始时间（ms）
        long l = System.currentTimeMillis();

        // 数据量大小
        int length = data.size();

        // 每个线程处理的数据个数
        int taskCount = length / threadCount;

        // 划分每个线程调用的数据
        for (int i = 0; i < threadCount; i++) {

            // 每个线程任务数据list
            List<T> subData;

            if (i == (threadCount - 1)) {

                subData = data.subList(i * taskCount, length);

            } else {

                subData = data.subList(i * taskCount, (i + 1) * taskCount);

            }
            // 将数据分配给各个线程
            HandleCallable execute = new HandleCallable<T>(String.valueOf(i), subData, params, task);

            // 将线程加入到线程池
            pool.submit(execute);
        }

        // 总的返回结果集
        List<ServiceResult> result = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {

            // 每个线程处理结果集
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
        // 关闭线程池
        threadpool.shutdownNow();
        // 执行结束时间
        long end_l = System.currentTimeMillis();
        log.info("cost time :{} ms", (end_l - l));
        return new ServiceResult(result);
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

}
