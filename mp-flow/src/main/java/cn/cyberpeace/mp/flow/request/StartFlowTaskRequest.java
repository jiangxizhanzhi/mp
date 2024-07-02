package cn.cyberpeace.mp.flow.request;

import cn.cyberpeace.mp.flow.bo.FlowTaskStart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName StartFlowTaskRequest
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StartFlowTaskRequest {

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
    private FlowTaskStart task;

}
