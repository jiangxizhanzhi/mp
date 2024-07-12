 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CollectedElementStatusBO.java.
 // File size 1027.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CollectedElementStatusBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectedElementStatusBO {
    @ApiModelProperty(value = "场景id")
    private String csceneId;
    @ApiModelProperty(value = "节点id")
    private String nodeId;
    @ApiModelProperty(value = "节点名称")
    private String name;
    @ApiModelProperty(value = "事件")
    private Integer event;
    @ApiModelProperty(value = "事件状态")
    private Integer eventStatus;
    @ApiModelProperty(value = "事件进程")
    private String process;
    @ApiModelProperty(value = "事件进程状态")
    private Integer processStatus;
    @ApiModelProperty(value = "事件进程描述信息")
    private String processMessage;
}