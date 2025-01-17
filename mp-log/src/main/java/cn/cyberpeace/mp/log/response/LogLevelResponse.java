 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogLevelResponse.java.
 // File size 629.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

/**
 * @description 日志级别响应
 * @author wangssh
 * @date 2023/5/30
 * @version 1.0
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogLevelResponse {

    @ApiModelProperty(value = "日志模块名")
    @NotBlank
    private String logName;

    @ApiModelProperty(value = "日志级别")
    private String logLevel;

}
