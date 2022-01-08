package cn.cyberpeace.mp.base.util.thread.taskpool;

import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.base.util.thread.ThreadUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
public class TaskPool {

    private final ExecutorService executor;

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
        var latch = new CountDownLatch(poolTasks.size());
        poolTasks.forEach(poolTask -> {
            Runnable command = () -> {
                invokeTask(poolTask);
                latch.countDown();

                if (!independent && TaskStatus.ERROR.equals(poolTask.getStatus())) {
                    poolTasks.forEach(itemTask -> interruptTask(itemTask, poolTask.getException()));
                }
            };
            if (format) {
                command = ThreadUtils.format(command);
            }
            executor.execute(command);
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("Interruption occurs, up to {} tasks will be cancelled", latch.getCount());
            poolTasks.forEach(poolTask -> interruptTask(poolTask, e));
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
        List<T1> poolTasks = tasks.stream()
                .map(task -> {
                    var poolTask = ClassUtils.createInstance(poolTaskClass);
                    poolTask.setTask(task);
                    return poolTask;
                })
                .collect(Collectors.toList());
        return invokePoolTasks(poolTasks, independent, format);
    }

    public TaskPool(int poolSize) {
        this(poolSize, Integer.MAX_VALUE);
    }

    public TaskPool(int poolSize, int holdSize) {
        this(poolSize, holdSize, 0L);
    }

    public TaskPool(int poolSize, int holdSize, long keepAliveTime) {
        executor = new ThreadPoolExecutor(poolSize, poolSize, keepAliveTime, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(holdSize - poolSize));
    }

    public TaskPool(ExecutorService executor) {
        this.executor = executor;
    }

    public void executeTask(Runnable task) {
        executeTask(task, true);
    }

    public void executeTask(Runnable task, boolean format) {
        var poolTask = new DefaultPoolTask(task);
        Runnable command = () -> invokeTask(poolTask);
        if (format) {
            command = ThreadUtils.format(command);
        }
        executor.execute(command);
    }

    public List<DefaultPoolTask> runTasks(List<Runnable> tasks) {
        return runTasks(tasks, false, true);
    }

    public List<DefaultPoolTask> runTasks(List<Runnable> tasks, boolean independent, boolean format) {
        return invokeTasks(tasks, independent, format, DefaultPoolTask.class);
    }

    public <C, T extends PoolTask<C>> List<T> runPoolTasks(List<T> poolTasks) {
        return runPoolTasks(poolTasks, false, true);
    }

    public <C, T extends PoolTask<C>> List<T> runPoolTasks(List<T> poolTasks, boolean independent, boolean format) {
        return invokePoolTasks(poolTasks, independent, format);
    }

    public <R> List<DefaultResultPoolTask<R>> callTasks(List<Callable<R>> tasks) {
        return callTasks(tasks, false, true);
    }

    public <R> List<DefaultResultPoolTask<R>> callTasks(List<Callable<R>> tasks, boolean independent, boolean format) {
        return invokeTasks(tasks, independent, format, new DefaultResultPoolTask<R>().getClass());
    }

    public <C, R, T extends ResultPoolTask<C, R>> List<T> callPoolTasks(List<T> poolTasks) {
        return callPoolTasks(poolTasks, false, true);
    }

    public <C, R, T extends ResultPoolTask<C, R>> List<T> callPoolTasks(List<T> poolTasks, boolean independent,
                                                                        boolean format) {
        return invokePoolTasks(poolTasks, independent, format);
    }

}
