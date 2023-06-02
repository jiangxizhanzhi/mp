package cn.cyberpeace.mp.flow.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 批量停止流量任务请求
 * @Author: guquan
 * @Date：2023/6/2 9:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BatchStopFlowTaskRequest {

    /**
     * 操作
     */
    private int action;

    /**
     * 流量任务启动对象
     */
    private List<String> taskIds;
}
