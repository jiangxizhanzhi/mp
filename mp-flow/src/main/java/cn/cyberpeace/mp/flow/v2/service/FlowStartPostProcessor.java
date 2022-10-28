package cn.cyberpeace.mp.flow.v2.service;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/25
 */
// todo 应该是一个定时任务形式  实现形式需要再考虑下
@Component
public class FlowStartPostProcessor {

    private static Queue<FlowCallBackTask> queue = new ConcurrentLinkedDeque<>();

    private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    @PostConstruct
    public void startActions() {
        executor.scheduleAtFixedRate(() -> {
            System.out.println("============ 流量任务后置执行器 启动 =============");
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                FlowCallBackTask task = queue.poll();
                try {
                    boolean result = task.action();
                    if (!result) {
                        addTask(task);
                    }
                } catch (Exception e) {
                    addTask(task);
                    e.printStackTrace();
                }
            }
            System.out.println("============ 流量任务后置执行器 停止 =============");
        }, 10, 10, TimeUnit.SECONDS);
    }

    public void addTask(FlowCallBackTask flowCallBackTask) {
        if (flowCallBackTask != null) {
            queue.offer(flowCallBackTask);
        }
    }

}
