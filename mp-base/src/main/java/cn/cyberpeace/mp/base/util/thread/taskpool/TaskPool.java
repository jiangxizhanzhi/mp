 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TaskPool.java.
 // File size 10043.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.util.thread.taskpool;

import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.base.util.thread.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @ClassName TaskPool
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
public class TaskPool {

    private final ExecutorService executor;

    public static final Set<TaskPool> TASK_POOL_INSTANCE_CACHE = new CopyOnWriteArraySet();

    /**
     * getTaskPoolInstanceCache
     *
     * @return
     */
    public static Set<TaskPool> getTaskPoolInstanceCache(){
        return Collections.unmodifiableSet(TASK_POOL_INSTANCE_CACHE);
    }

    /**
     * getExecutorService
     *
     * @return
     */
    public ThreadPoolExecutor getExecutorService(){
        return (ThreadPoolExecutor)executor;
    }

    /**
     * @description: 线程工厂
     * @param: threadName
     * @return: java.util.concurrent.ThreadFactory
     * @date: 2023/6/29
     */
    public ThreadFactory createNameThreadFactory(String threadName){
        if(StringUtils.isEmpty(threadName)){
            //如果不传，使用当前调用类名作为线程池前缀
            threadName = this.getClass().getName().toLowerCase().concat("-");
        }
        if(!threadName.endsWith("-")){
            threadName = threadName.concat("-");
        }
        return new CustomizableThreadFactory(threadName);
    }

    /**
     * invokeTask
     *
     * @param poolTask
     */
    public static <C, T, R> void invokeTask(BasePoolTask<C, T, R> poolTask) {
        if (poolTask.getStatus() == null) {
            return;
        }

        if (!poolTask.getStatus().equals(TaskStatus.WAITING)) {
            return;
        }

        poolTask.setStatus(TaskStatus.RUNNING);
        try {
            poolTask.invoke();
            poolTask.setStatus(TaskStatus.OVER);
        } catch (Exception e) {
            log.error("Task run error: ", e);
            poolTask.setStatus(TaskStatus.ERROR);
            poolTask.setException(e);
        }
    }

    /**
     * interruptTask
     *
     * @param poolTask
     * @param e
     */
    public static void interruptTask(BasePoolTask<?, ?, ?> poolTask, Exception e) {
        if (TaskStatus.WAITING.equals(poolTask.getStatus())) {
            poolTask.setStatus(TaskStatus.INTERRUPTED);
            poolTask.setException(e);
        }
    }

    /**
     * 批量运行任务，多个任务为一个整体
     * @param poolTasks 任务
     * @param independent 任务间是否独立，false情况下任意一个任务的异常会取消掉所有队列任务
     * @param format 是否格式化标准任务
     * @return 任务执行结果
     */
    private  <C, T, R, T1 extends BasePoolTask<C, T, R>> List<T1> invokePoolTasks(List<T1> poolTasks,
                                                                                  boolean independent, boolean format) {
        CountDownLatch latch = new CountDownLatch(poolTasks.size());
        for (T1 t1 : poolTasks) {
            Runnable command = () -> {
                invokeTask(t1);
                latch.countDown();

                if (!independent && TaskStatus.ERROR.equals(t1.getStatus())) {
                    for (T1 itemTask : poolTasks) {
                        interruptTask(itemTask, t1.getException());
                    }
                }
            };
            if (format) {
                command = ThreadUtils.format(command);
            }
            executor.execute(command);
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("Interruption occurs, up to {} tasks will be cancelled", latch.getCount());
            for (T1 poolTask : poolTasks) {
                interruptTask(poolTask, e);
            }
            Thread.currentThread().interrupt();
        }

        return poolTasks;
    }

    /**
     * 批量运行任务，多个任务为一个整体
     * @param tasks 任务
     * @param independent 任务间是否独立，false情况下任意一个任务的异常会取消掉所有队列任务
     * @param format 是否格式化标准任务
     * @param poolTaskClass 任务类型
     * @return 任务执行结果
     */
    private <C, T, R, T1 extends BasePoolTask<C, T, R>> List<T1> invokeTasks(List<T> tasks, boolean independent,
                                                                             boolean format, Class<T1> poolTaskClass) {
        List<T1> poolTasks = new ArrayList<>();
        for (T t : tasks) {
            T1 poolTask = ClassUtils.createInstance(poolTaskClass);
            poolTask.setTask(t);
            T1 apply = poolTask;
            poolTasks.add(apply);
        }
        return invokePoolTasks(poolTasks, independent, format);
    }

    /**
     * TaskPool
     *
     * @param poolSize
     */
    public TaskPool(int poolSize) {
        this(poolSize, Integer.MAX_VALUE);
    }

    /**
     * TaskPool
     *
     * @param poolSize
     * @param holdSize
     */
    public TaskPool(int poolSize, int holdSize) {
        this(poolSize, holdSize, 0L);
    }

    /**
     * TaskPool
     *
     * @param poolSize
     * @param holdSize
     * @param keepAliveTime
     * @param factory
     */
    public TaskPool(int poolSize, int holdSize, long keepAliveTime, ThreadFactory factory) {
        executor = new ThreadPoolExecutor(poolSize, poolSize, keepAliveTime, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(holdSize - poolSize), factory);
        TASK_POOL_INSTANCE_CACHE.add(this);
    }

    /**
     * TaskPool
     *
     * @param poolSize
     * @param holdSize
     * @param keepAliveTime
     */
    public TaskPool(int poolSize, int holdSize, long keepAliveTime) {
        executor = new ThreadPoolExecutor(poolSize, poolSize, keepAliveTime, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(holdSize - poolSize), createNameThreadFactory(""));
        TASK_POOL_INSTANCE_CACHE.add(this);
    }

    /**
     * TaskPool
     *
     * @param executor
     */
    public TaskPool(ExecutorService executor) {
        this.executor = executor;
    }

    /**
     * executeTask
     *
     * @param task
     */
    public void executeTask(Runnable task) {
        executeTask(task, true);
    }

    /**
     * executeTask
     *
     * @param task
     * @param format
     */
    public void executeTask(Runnable task, boolean format) {
        DefaultPoolTask poolTask = new DefaultPoolTask(task);
        Runnable command = () -> invokeTask(poolTask);
        if (format) {
            command = ThreadUtils.format(command);
        }
        executor.execute(command);
    }

    /**
     * runTasks
     *
     * @param tasks
     * @return
     */
    public List<DefaultPoolTask> runTasks(List<Runnable> tasks) {
        return runTasks(tasks, false, true);
    }

    /**
     * runTasks
     *
     * @param tasks
     * @param independent
     * @param format
     * @return
     */
    public List<DefaultPoolTask> runTasks(List<Runnable> tasks, boolean independent, boolean format) {
        return invokeTasks(tasks, independent, format, DefaultPoolTask.class);
    }

    /**
     * runPoolTasks
     *
     * @param poolTasks
     * @return
     */
    public <C, T extends PoolTask<C>> List<T> runPoolTasks(List<T> poolTasks) {
        return runPoolTasks(poolTasks, false, true);
    }

    /**
     * runPoolTasks
     *
     * @param poolTasks
     * @param independent
     * @param format
     * @return
     */
    public <C, T extends PoolTask<C>> List<T> runPoolTasks(List<T> poolTasks, boolean independent, boolean format) {
        return invokePoolTasks(poolTasks, independent, format);
    }

    /**
     * callTasks
     *
     * @param tasks
     * @return
     */
    public <R> List<DefaultResultPoolTask<R>> callTasks(List<Callable<R>> tasks) {
        return callTasks(tasks, false, true);
    }

    /**
     * callTasks
     *
     * @param tasks
     * @param independent
     * @param format
     * @return
     */
    public <R> List<DefaultResultPoolTask<R>> callTasks(List<Callable<R>> tasks, boolean independent, boolean format) {
        return invokeTasks(tasks, independent, format, new DefaultResultPoolTask<R>().getClass());
    }

    /**
     * callPoolTasks
     *
     * @param poolTasks
     * @return
     */
    public <C, R, T extends ResultPoolTask<C, R>> List<T> callPoolTasks(List<T> poolTasks) {
        return callPoolTasks(poolTasks, false, true);
    }

    /**
     * callPoolTasks
     *
     * @param poolTasks
     * @param independent
     * @param format
     * @return
     */
    public <C, R, T extends ResultPoolTask<C, R>> List<T> callPoolTasks(List<T> poolTasks, boolean independent,
                                                                        boolean format) {
        return invokePoolTasks(poolTasks, independent, format);
    }


    /**
     * equals
     *
     * @param o
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TaskPool taskPool = (TaskPool) o;
        return executor.equals(taskPool.executor);
    }

    /**
     * hashCode
     *
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(executor);
    }
}
