package cn.cyberpeace.mp.base.util.thread;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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

    private void runTask(PoolTask<Runnable> poolTask) {
        if (poolTask.getStatus() == null) {
            return;
        }

        if (!poolTask.getStatus().equals(TaskStatus.WAITING)) {
            return;
        }

        poolTask.setStatus(TaskStatus.RUNNING);
        try {
            poolTask.getTask().run();
            poolTask.setStatus(TaskStatus.OVER);
        } catch (Exception e) {
            log.error("Task run error: ", e);
            poolTask.setStatus(TaskStatus.ERROR);
            poolTask.setException(e);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PoolTask<T> {
        private T task;
        private Integer status;
        private Exception exception;
    }

    public TaskPool(int poolSize, int holdSize) {
        this(poolSize, holdSize, 0L);
    }

    public TaskPool(int poolSize, int holdSize, long keepAliveTime) {
        executor = new ThreadPoolExecutor(poolSize, poolSize, keepAliveTime, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(holdSize - poolSize));
    }

    public PoolTask<Runnable> runTask(Runnable task) {
        var poolTask = new PoolTask<>(task, TaskStatus.WAITING, null);
        executor.execute(ThreadUtils.format(() -> runTask(poolTask)));
        return poolTask;
    }

    public List<PoolTask<Runnable>> runTasks(List<Runnable> tasks) {
        var latch = new CountDownLatch(tasks.size());
        List<PoolTask<Runnable>> poolTasks = tasks.stream()
                .map(task -> new PoolTask<>(task, TaskStatus.WAITING, null))
                .collect(Collectors.toList());
        poolTasks.forEach(poolTask -> executor.execute(ThreadUtils.format(() -> {
            runTask(poolTask);
            latch.countDown();
        })));

        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("Interruption occurs, up to {} tasks will be cancelled", latch.getCount());
            poolTasks.forEach(poolTask -> {
                poolTask.setStatus(TaskStatus.INTERRUPTED);
                poolTask.setException(e);
            });
            Thread.currentThread().interrupt();
        }

        return poolTasks;
    }

}
