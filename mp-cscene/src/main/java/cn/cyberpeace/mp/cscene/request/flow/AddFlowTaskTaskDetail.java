 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AddFlowTaskTaskDetail.java.
 // File size 717.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.request.flow;

import cn.cyberpeace.mp.flow.bo.AddFlowTaskFlowInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName AddFlowTaskTaskDetail
 * @Description
 * @Author guquan
 * @Date 2023/5/29 17:14
 * @Version 1.0.0
 */
@Data
@ApiModel(value = "新增流量执行任务详情")
public class AddFlowTaskTaskDetail {

    /**
     * 节点ID
     */
    @ApiModelProperty(value = "节点ID")
    private String nodeId;

    /**
     * 流量集合
     */
    @ApiModelProperty(value = "流量集合")
    private List<AddFlowTaskFlowInfo> flowInfos;
}
