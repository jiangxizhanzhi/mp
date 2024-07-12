 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name StartFlowTaskRequest.java.
 // File size 666.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
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
