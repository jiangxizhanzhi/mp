package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool;

import cn.cyberpeace.mp.base.util.thread.TaskPool;
import lombok.Data;

import java.util.List;

@Data
public abstract class CloudTaskPool {

    private TaskPool taskPool;

    public List<TaskPool.PoolTask> runTasks(List<Runnable> tasks) {
        return taskPool.runTasks(tasks);
    }

}
