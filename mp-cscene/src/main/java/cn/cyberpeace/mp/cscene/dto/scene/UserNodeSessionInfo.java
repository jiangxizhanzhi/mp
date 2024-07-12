 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserNodeSessionInfo.java.
 // File size 1064.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.dto.scene;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName UserNodeSessionInfo
 * @Description
 * @Author maojun
 * @Date 2022/1/26
 * @Version 1.0.0
 */
@ApiModel(description = "场景接入会话数据")
@Data
public class UserNodeSessionInfo {
    @ApiModelProperty(value = "场景id")
    private String sceneId;
    @ApiModelProperty(value = "场景名称")
    private String sceneName;
    @ApiModelProperty(value = "节点id")
    private String nodeId;
    @ApiModelProperty(value = "节点名称")
    private String nodeName;
    @ApiModelProperty(value = "会话用户")
    private String user;
    @ApiModelProperty(value = "会话用户名称")
    private String userName;
    @ApiModelProperty(value = "会话协议")
    private String protocol;
    @ApiModelProperty(value = "监控url")
    private String monitorUrl;
    @ApiModelProperty(value = "协助url")
    private String assistanceUrl;
}
