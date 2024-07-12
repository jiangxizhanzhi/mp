 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AddFlowTaskTaskInfo.java.
 // File size 979.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName AddFlowTaskTaskInfo
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@ApiModel(value = "新增流量执行任务详情")
public class AddFlowTaskTaskInfo {

    /**
     * 节点ID
     */
    @ApiModelProperty(value = "节点ID")
    private String nodeId;

    /**
     * 节点名称
     */
    @ApiModelProperty(value = "节点名称")
    private String nodeName;

    /**
     * 节点IP
     */
    @ApiModelProperty(value = "节点IP")
    private String nodeIp;

    /**
     * 网络ID
     */
    @ApiModelProperty(value = "网络ID")
    private String networkId;

    /**
     * 流量集合
     */
    @ApiModelProperty(value = "流量集合")
    private List<AddFlowTaskFlowInfo> flowInfos;
}
