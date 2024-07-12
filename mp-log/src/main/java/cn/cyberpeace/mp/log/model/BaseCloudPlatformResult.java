 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseCloudPlatformResult.java.
 // File size 1013.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.log.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName BaseCloudPlatformResult
 * @Description 基础模型，对应云平台接口返回信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
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
