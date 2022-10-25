package cn.cyberpeace.mp.flow.v2.service;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Vector;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/25
 */
// todo 应该是一个定时任务形式
@Component
public class FlowStartPostProcessor {

    private static List<FlowCallBackTask> tasks = new Vector<>();

    public void addTask(FlowCallBackTask flowCallBackTask) {
        tasks.add(flowCallBackTask);
    }

    public void handle() {
        for (var task : tasks) {
            task.action();
        }
    }
}
