package cn.cyberpeace.mp.base.cron;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class TaskInfo {

    private static Map<String, TaskInfo> TASK_INFOS = new ConcurrentHashMap<>();

    private String taskId;

    private String description;

    private String cron;

    private Runnable job;

    public TaskInfo(String taskId, String description, String cron, Runnable job) {
        this.taskId = taskId;
        this.description = description;
        this.cron = cron;
        this.job = job;
    }

    public String getShowInfo() {
        return String.format("id:%s,desc:%s,cron:%s", taskId, description, cron);
    }

}
