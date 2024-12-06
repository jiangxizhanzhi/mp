// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name CsceneInstanceNodeCommandActionRequest.java.
// File size 575.
// File create date Fri Jul 12 12:55:22 CST 2024.
// File update date Fri Jul 12 12:55:22 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName CsceneInstanceNodeCommandActionRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CsceneInstanceNodeActionRequest
 */
@Data
public class NodeCommandQgaActionRequest {
    @ApiModelProperty(value = "场景id")
    private String id;
    @ApiModelProperty(value = "节点id")
    private String nodeId;
    @ApiModelProperty(value = "执行命令")
    private String execCmd;
    @ApiModelProperty(value = "命令参数")
    private List<String> args;
    @ApiModelProperty(value = "")
    private List<String> envs;
}
