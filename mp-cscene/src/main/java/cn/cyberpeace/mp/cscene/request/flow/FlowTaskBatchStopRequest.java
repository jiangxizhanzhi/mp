 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowTaskBatchStopRequest.java.
 // File size 522.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.request.flow;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName FlowTaskBatchStopRequest
 * @Description 流量任务批量停止请求
 * @Author guquan
 * @Date 2023/6/2 8:59
 * @Version 1.0.0
 */
@Data
@ApiModel("流量任务批量停止请求")
public class FlowTaskBatchStopRequest {

    @ApiModelProperty("任务id集合")
    private List<String> taskIds;
}
