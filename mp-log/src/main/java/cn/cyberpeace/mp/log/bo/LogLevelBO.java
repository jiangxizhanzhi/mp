 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogLevelBO.java.
 // File size 847.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.log.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @ClassName LogLevelBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogLevelBO implements Serializable {

    private static final long serialVersionUID = 3260069067526609727L;

    /**
     * 默认全局模块ROOT，其他模块包名
     */
    @ApiModelProperty(value = "日志模块名")
    @NotBlank
    private String logName;

    @ApiModelProperty(value = "日志级别")
    private String logLevel;

}
