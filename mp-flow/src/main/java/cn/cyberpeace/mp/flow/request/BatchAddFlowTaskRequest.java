 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BatchAddFlowTaskRequest.java.
 // File size 595.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName BatchAddFlowTaskRequest
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@ApiModel(value = "批量新增流量任务请求")
public class BatchAddFlowTaskRequest {

    /**
     * 新增流量任务请求集合
     */
    @ApiModelProperty(value = "新增流量任务请求集合")
    private List<AddFlowTaskRequest> addTaskRequestList;

}
