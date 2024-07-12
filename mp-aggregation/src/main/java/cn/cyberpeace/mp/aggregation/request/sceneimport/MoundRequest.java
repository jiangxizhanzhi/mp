 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MoundRequest.java.
 // File size 489.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.request.sceneimport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName MoundRequest
 * @Description
 * @Author maojun
 * @Date 2022/6/21
 * @Version 1.0.0
 */
@Data
@ApiModel("挂载请求")
public class MoundRequest {
    @ApiModelProperty(value = "主机")
    private String host;

    @ApiModelProperty(value = "路径")
    private String path;
}
