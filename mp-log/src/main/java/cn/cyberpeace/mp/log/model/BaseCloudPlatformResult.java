 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseCloudPlatformResult.java.
 // File size 883.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 基础模型，对应云平台接口返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseCloudPlatformResult extends BaseModel {

    /**
     * 是否成功
     */
    @ApiModelProperty(hidden = true)
    private boolean success;

    /**
     * 请求ID
     */
    @ApiModelProperty(hidden = true)
    private String requestId;

    /**
     * 总数据大小
     */
    @ApiModelProperty(hidden = true)
    private Integer total = 0;

    /**
     * 消息体
     */
    @ApiModelProperty(hidden = true)
    private String message;

    /**
     * code
     */
    @ApiModelProperty(hidden = true)
    private String code;
}
