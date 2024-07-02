package cn.cyberpeace.mp.flow.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName BatchStopFlowTaskRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
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
