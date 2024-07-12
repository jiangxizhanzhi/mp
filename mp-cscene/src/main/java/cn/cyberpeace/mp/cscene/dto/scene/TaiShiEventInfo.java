 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TaiShiEventInfo.java.
 // File size 2073.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.dto.scene;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName TaiShiEventInfo
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "场景态势事件")
public class TaiShiEventInfo {
    @ApiModelProperty(value = "场景id")
    private String id;

    @ApiModelProperty(value = "事件名称")
    private String eventName;

    @ApiModelProperty(value = "触发类型,host:主机采集,flow:流量采集")
    private String triggerType;

    @ApiModelProperty(value = "动作类型,attack:攻击,defense:防御")
    private String actionType;

    @ApiModelProperty(value = "源节点id")
    private String sourceNodeId;

    @ApiModelProperty(value = "源节点名称")
    private String sourceNodeName;

    @ApiModelProperty(value = "源节点ip")
    private String sourceNodeIp;

    @ApiModelProperty(value = "源节点端口")
    private String sourceNodePort;

    @ApiModelProperty(value = "目的点端口")
    private String targetNodeId;

    @ApiModelProperty(value = "目的点名称")
    private String targetNodeName;

    @ApiModelProperty(value = "目的点ip")
    private String targetNodeIp;

    @ApiModelProperty(value = "目的点端口")
    private String targetNodePort;

    @ApiModelProperty(value = "att&ck事件,即攻防类型")
    private List<String> attCks;

    @ApiModelProperty(value = "att&ck事件名称,即攻防类型")
    private List<String> attCkNames;

    @ApiModelProperty(value = "事件类型")
    private String hostEventType;

    @ApiModelProperty(value = "事件描述")
    private String hostEventDesc;

    @ApiModelProperty(value = "时间")
    private LocalDateTime time;
}
