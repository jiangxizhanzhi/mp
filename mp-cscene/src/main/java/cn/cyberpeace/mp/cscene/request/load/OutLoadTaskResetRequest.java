package cn.cyberpeace.mp.cscene.request.load;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务重置
 */
@Data
@NoArgsConstructor
public class OutLoadTaskResetRequest {

    /**
     * 任务id
     */
    private String taskId;
}