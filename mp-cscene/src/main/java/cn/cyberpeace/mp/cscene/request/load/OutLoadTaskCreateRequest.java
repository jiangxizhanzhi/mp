package cn.cyberpeace.mp.cscene.request.load;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务创建
 */
@Data
@NoArgsConstructor
public class OutLoadTaskCreateRequest {

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务描述
     */
    private String taskDescribe;

    /**
     * 工具id
     */
    private String toolId;

    /**
     * 执行命令
     */
    private String execCommand;
}