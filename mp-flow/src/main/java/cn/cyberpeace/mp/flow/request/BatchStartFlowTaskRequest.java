 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BatchStartFlowTaskRequest.java.
 // File size 802.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.request;

import cn.cyberpeace.mp.flow.bo.FlowTaskStart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName BatchStartFlowTaskRequest
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
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

    /**
     * 执行方式：0 串行，1 并行
     */
    private Integer runType;

}
