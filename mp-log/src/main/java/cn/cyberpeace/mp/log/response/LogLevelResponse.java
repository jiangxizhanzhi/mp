 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogLevelResponse.java.
 // File size 643.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.log.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

/**
 * @ClassName LogLevelResponse
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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
