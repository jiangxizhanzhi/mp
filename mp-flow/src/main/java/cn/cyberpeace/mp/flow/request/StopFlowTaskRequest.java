package cn.cyberpeace.mp.flow.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 停止流量任务请求
 * @Author: guquan
 * @Date：2023/6/2 9:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StopFlowTaskRequest {

    /**
     * 操作
     */
    private int action;

    /**
     * 任务ID
     */
    private String taskId;
}
