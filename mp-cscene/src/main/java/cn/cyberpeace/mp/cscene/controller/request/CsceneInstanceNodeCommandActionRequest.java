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

/**
 * @ClassName CsceneInstanceNodeCommandActionRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CsceneInstanceNodeActionRequest
 */
@Data
public class CsceneInstanceNodeCommandActionRequest extends CsceneInstanceNodeActionRequest {
    @ApiModelProperty(value = "执行命令")
    private String command;
    @ApiModelProperty(value = "执行命令的用户")
    private String execUser;
}
