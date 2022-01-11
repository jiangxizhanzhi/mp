package cn.cyberpeace.mp.base.util.thread.taskpool;

import cn.cyberpeace.mp.base.util.thread.ThreadUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 延迟任务执行
 */
@Slf4j
public class DelayTaskPool {

    public static final Integer DEFAULT_RUN_DELAY = 2000;

    public static final Integer DEFAULT_POOL_SIZE = 10;

    private final TaskPool taskPool;

    private final TaskPool runPool;

    /**
     * 任务集合<任务key, 任务>，相同任务key会覆盖
     */
    private final Map<String, Runnable> taskMap = new ConcurrentHashMap<>();

    private Integer runDelay;

    private final RunStatus status = new RunStatus();

    @Data
    private static class RunStatus {
        private boolean pending = false;
        private boolean running = false;
    }

    public DelayTaskPool() {
        this(new TaskPool(DEFAULT_POOL_SIZE));
    }

    public DelayTaskPool(TaskPool taskPool) {
        this(taskPool, DEFAULT_RUN_DELAY);
    }

    public DelayTaskPool(TaskPool taskPool, int runDelay) {
        this.taskPool = taskPool;
        this.runPool = new TaskPool(2);
        this.runDelay = runDelay;
    }

    /**
     * 添加任务
     * @param key 任务key
     * @param task 任务
     */
    public void add(String key, Runnable task) {
        Runnable formatTask = ThreadUtils.format(task);
        synchronized (taskMap) {
            taskMap.put(key, formatTask);
        }
    }

    /**
     * 添加并执行任务
     * @param key 任务key
     * @param task 任务
     */
    public void execute(String key, Runnable task) {
        add(key, task);
        runPool.executeTask(this::run);
    }

    /**
     * 执行任务，执行动作为同步操作
     */
    public void run() {
        synchronized (status) {
            if (status.isRunning()) {
                // 如果任务在运行中，设置下一次执行请求标记，退出
                status.setPending(true);
                return;
            }
            status.setRunning(true);
        }

        // 运行任务
        try {
            Thread.sleep(runDelay);
        } catch (InterruptedException e) {
            log.error("Delay task pool run delay is interrupted: ", e);
        }
        Map<String, Runnable> runTaskMap = null;
        synchronized (taskMap) {
            if (!taskMap.isEmpty()) {
                runTaskMap = new HashMap<>(taskMap);
                taskMap.clear();
            }
        }
        if (runTaskMap != null) {
            runTaskMap.forEach((key, task) -> taskPool.executeTask(task, false));
        }

        var runNext = false;
        synchronized (status) {
            status.setRunning(false);
            // 运行任务完成后，判断是否存在执行请求
            if (status.isPending()) {
                // 存在执行请求, 继续运行任务
                status.setPending(false);
                runNext = true;
            }
        }

        if (runNext) {
            run();
        }
    }

}
