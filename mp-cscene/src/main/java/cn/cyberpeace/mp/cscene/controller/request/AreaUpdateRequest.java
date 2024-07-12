 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AreaUpdateRequest.java.
 // File size 516.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName AreaUpdateRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class AreaUpdateRequest {
    @ApiModelProperty(value = "场景id")
    private String sceneId;
    @ApiModelProperty(value = "区域id")
    private String areaId;
    @ApiModelProperty(value = "显示参数")
    private String displayContext;
}
