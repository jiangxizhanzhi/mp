package cn.cyberpeace.mp.base.common;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.util.thread.taskpool.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Callable;

@Component
public class TaskCommon {

    public static TaskCommonPool taskCommonPool;

    private TaskCommon(TaskCommonPool taskCommonPool) {
        TaskCommon.taskCommonPool = taskCommonPool;
    }

    public static void executeTask(Runnable task) {
        taskCommonPool.executeTask(task);
    }

    public static List<DefaultPoolTask> runTasks(List<Runnable> tasks) {
        return taskCommonPool.runTasks(tasks);
    }

    public static List<DefaultPoolTask> runTasks(List<Runnable> tasks, boolean independent, boolean format) {
        return taskCommonPool.runTasks(tasks, independent, format);
    }

    public static <C, T extends PoolTask<C>> List<T> runPoolTasks(List<T> poolTasks) {
        return taskCommonPool.runPoolTasks(poolTasks);
    }

    public static <C, T extends PoolTask<C>> List<T> runPoolTasks(List<T> poolTasks, boolean independent,
                                                                  boolean format) {
        return taskCommonPool.runPoolTasks(poolTasks, independent, format);
    }

    public static <R> List<DefaultResultPoolTask<R>> callTasks(List<Callable<R>> tasks) {
        return taskCommonPool.callTasks(tasks);
    }

    public static <R> List<DefaultResultPoolTask<R>> callTasks(List<Callable<R>> tasks, boolean independent,
                                                               boolean format) {
        return taskCommonPool.callTasks(tasks, independent, format);
    }

    public static <C, R, T extends ResultPoolTask<C, R>> List<T> callPoolTasks(List<T> poolTasks) {
        return taskCommonPool.callPoolTasks(poolTasks);
    }

    public static <C, R, T extends ResultPoolTask<C, R>> List<T> callPoolTasks(List<T> poolTasks, boolean independent,
                                                                               boolean format) {
        return taskCommonPool.callPoolTasks(poolTasks, independent, format);
    }

    @Component
    public static class TaskCommonPool extends TaskPool {
        public TaskCommonPool(BaseAppConfig baseAppConfig) {
            super(baseAppConfig.getTaskPoolSize(), baseAppConfig.getTaskHoldSize(), 60L);
        }
    }
}
