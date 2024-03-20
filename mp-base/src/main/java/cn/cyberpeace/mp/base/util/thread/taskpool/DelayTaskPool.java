package cn.cyberpeace.mp.base.util.thread.taskpool;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 延迟任务执行
 */
@Slf4j
public class DelayTaskPool {

    public static final Integer DEFAULT_RUN_DELAY = 2000;

    public static final Integer DEFAULT_POOL_SIZE = 10;

    public static final String DEFAULT_TASK_SUB_KEY = "_";

    private final TaskPool taskPool;

    private final TaskPool runPool;

    /**
     * 任务集合<任务key, 有序任务集合>
     * 一个任务key下，可以有多个有序任务
     */
    private final Map<String, LinkedHashMap<String, Runnable>> taskMap = new ConcurrentHashMap<>();

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
     * @param subKey 任务子key
     * @param task 任务
     */
    public void add(String key, String subKey, Runnable task) {
        synchronized (taskMap) {
            var orderedTasks = taskMap.getOrDefault(key, null);
            if (orderedTasks == null) {
                orderedTasks = new LinkedHashMap<>();
            }
            orderedTasks.remove(subKey);
            orderedTasks.put(subKey, task);
            taskMap.put(key, orderedTasks);
        }
    }

    /**
     * 添加任务
     * @param key 任务key
     * @param task 任务
     */
    public void add(String key, Runnable task) {
        add(key, DEFAULT_TASK_SUB_KEY, task);
    }

    /**
     * 添加并执行任务
     * @param key 任务key
     * @param subKey 任务子key
     * @param task 任务
     */
    public void execute(String key, String subKey, Runnable task) {
        add(key, subKey, task);
        runPool.executeTask(this::run);
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
        Map<String, LinkedHashMap<String, Runnable>> runTaskMap = null;
        synchronized (taskMap) {
            if (!taskMap.isEmpty()) {
                runTaskMap = new HashMap<>(taskMap);
                taskMap.clear();
            }
        }
        if (runTaskMap != null) {
            runTaskMap.forEach((key, orderedTasks) -> {
                Runnable orderedTasksFunc = () -> {
                    orderedTasks.forEach((subKey, task) -> task.run());
                };
                taskPool.executeTask(orderedTasksFunc, true);
            });
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
