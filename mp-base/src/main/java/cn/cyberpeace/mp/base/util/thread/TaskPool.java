package cn.cyberpeace.mp.base.util.thread;

import cn.cyberpeace.mp.base.util.ClassUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
public class TaskPool {

    private final ThreadPoolExecutor executor;

    private static class TaskStatus {
        public static final Integer WAITING = 0;
        public static final Integer RUNNING = 1;
        public static final Integer OVER = 2;
        public static final Integer ERROR = 3;
        public static final Integer INTERRUPTED = 4;
    }

    private <T, R> void invokeTask(BasePoolTask<T, R> poolTask) {
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

    private void interruptTask(BasePoolTask<?, ?> poolTask, Exception e) {
        if (TaskStatus.WAITING.equals(poolTask.getStatus())) {
            poolTask.setStatus(TaskStatus.INTERRUPTED);
            poolTask.setException(e);
        }
    }

    /**
     * 批量运行任务，多个任务为一个整体
     * @param tasks 任务
     * @param independent 任务间是否独立，false情况下任意一个任务的异常会取消掉所有队列任务
     * @return 任务执行结果
     */
    private  <T, R, C extends BasePoolTask<T, R>> List<C> invokeTasks(List<T> tasks, boolean independent,
                                                                      Class<C> poolTaskClass) {
        var latch = new CountDownLatch(tasks.size());
        List<C> poolTasks = tasks.stream()
                .map(task -> {
                    var poolTask = ClassUtils.createInstance(poolTaskClass);
                    poolTask.setTask(task);
                    return poolTask;
                })
                .collect(Collectors.toList());
        poolTasks.forEach(poolTask -> executor.execute(ThreadUtils.format(() -> {
            invokeTask(poolTask);
            latch.countDown();

            if (!independent && TaskStatus.ERROR.equals(poolTask.getStatus())) {
                poolTasks.forEach(itemTask -> interruptTask(itemTask, poolTask.getException()));
            }
        })));

        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("Interruption occurs, up to {} tasks will be cancelled", latch.getCount());
            poolTasks.forEach(poolTask -> interruptTask(poolTask, e));
            Thread.currentThread().interrupt();
        }

        return poolTasks;
    }

    @Data
    public abstract static class BasePoolTask<T, R> {
        private T task;
        private Integer status = TaskStatus.WAITING;
        private Exception exception;
        private R result;

        public boolean isError() {
            return TaskStatus.ERROR.equals(status);
        }

        public boolean isInterrupted() {
            return TaskStatus.INTERRUPTED.equals(status);
        }

        public abstract void invoke() throws Exception;
    }

    @Data
    @NoArgsConstructor
    public static class PoolTask extends BasePoolTask<Runnable, Object> {
        public PoolTask(Runnable task) {
            setTask(task);
        }

        @Override
        public void invoke() {
            getTask().run();
        }
    }

    @Data
    @NoArgsConstructor
    public static class ResultPoolTask<R> extends BasePoolTask<Callable<R>, R> {
        public ResultPoolTask(Callable<R> task) {
            setTask(task);
        }

        @Override
        public void invoke() throws Exception {
            setResult(getTask().call());
        }
    }

    public TaskPool(int poolSize, int holdSize) {
        this(poolSize, holdSize, 0L);
    }

    public TaskPool(int poolSize, int holdSize, long keepAliveTime) {
        executor = new ThreadPoolExecutor(poolSize, poolSize, keepAliveTime, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(holdSize - poolSize));
    }

    public void executeTask(Runnable task) {
        var poolTask = new PoolTask(task);
        executor.execute(ThreadUtils.format(() -> invokeTask(poolTask)));
    }

    public List<PoolTask> runTasks(List<Runnable> tasks) {
        return runTasks(tasks, false);
    }

    public List<PoolTask> runTasks(List<Runnable> tasks, boolean independent) {
        return invokeTasks(tasks, independent, PoolTask.class);
    }

    public <R> List<ResultPoolTask<R>> callTasks(List<Callable<R>> tasks, Class<ResultPoolTask<R>> poolTaskClass) {
        return callTasks(tasks, poolTaskClass, false);
    }

    public <R> List<ResultPoolTask<R>> callTasks(List<Callable<R>> tasks, Class<ResultPoolTask<R>> poolTaskClass,
                                                 boolean independent) {
        return invokeTasks(tasks, independent, poolTaskClass);
    }

}
