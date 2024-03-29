package cn.cyberpeace.mp.base.cron;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@Slf4j
public class TaskInfo {

    private static Map<String, TaskInfo> TASK_INFOS = new ConcurrentHashMap<>();

    private String taskId;

    private String description;

    private String cron;

    private int initialDelaySeconds;

    private MethodTask methodTask;

    public TaskInfo(String taskId, String description, String cron, int initialDelaySeconds, MethodTask methodTask) {
        this.taskId = taskId;
        this.description = description;
        this.cron = cron;
        this.initialDelaySeconds = initialDelaySeconds;
        this.methodTask = methodTask;
    }

    public String getShowInfo() {
        return String.format("id:%s,desc:%s,cron:%s", taskId, description, cron);
    }

    public static class MethodTask {

        private Method method;

        private Object bean;

        public MethodTask(Method method, Object bean) {
            this.method = method;
            this.bean = bean;
        }

        public void action() {
            try {
                method.invoke(bean);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
