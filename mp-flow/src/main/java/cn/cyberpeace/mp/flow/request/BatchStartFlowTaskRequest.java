package cn.cyberpeace.mp.flow.request;

import cn.cyberpeace.mp.flow.bo.FlowTaskStart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 批量启动流量任务请求
 * @Author qiaokang
 * @Date：2023/5/24 17:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BatchStartFlowTaskRequest {

    /**
     * 操作
     */
    private int action;

    /**
     * 当前时间: "2023-05-10 11:30:20"
     */
    private String time;

    /**
     * 流量任务启动对象
     */
    private List<FlowTaskStart> tasks;

}
