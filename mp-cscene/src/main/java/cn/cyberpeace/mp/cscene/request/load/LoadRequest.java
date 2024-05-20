package cn.cyberpeace.mp.cscene.request.load;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 载荷执行结果批量新增
 */
@Data
@NoArgsConstructor
public class LoadRequest{

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 结果日志
     */
    private String resultLog;

    /**
     * 结果状态
     */
    private String resultStatus;
}