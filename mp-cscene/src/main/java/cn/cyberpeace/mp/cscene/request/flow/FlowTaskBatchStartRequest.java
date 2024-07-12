 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowTaskBatchStartRequest.java.
 // File size 528.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.request.flow;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName FlowTaskBatchStartRequest
 * @Description 流量任务批量启动请求
 * @Author guquan
 * @Date 2023/5/26 10:48
 * @Version 1.0.0
 */
@Data
@ApiModel("流量任务批量启动请求")
public class FlowTaskBatchStartRequest {

    @ApiModelProperty("任务id集合")
    private List<String> taskIds;

}
