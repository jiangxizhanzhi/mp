package cn.cyberpeace.mp.cscene.request.load;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务修改
 */
@Data
@NoArgsConstructor
public class OutLoadTaskUpdateRequest {

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 任务描述
     */
    private String taskDescribe;

    /**
     * 访问命令
     */
    private String execCommand;
}