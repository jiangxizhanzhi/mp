 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name EventTrackRequest.java.
 // File size 678.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName EventTrackRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventTrackRequest {
    @ApiModelProperty(value = "模块")
    private String module;
    @ApiModelProperty(value = "分类")
    private String category;
    @ApiModelProperty(value = "动作")
    private String action;
    @ApiModelProperty(value = "操作对象")
    private String target;
}
