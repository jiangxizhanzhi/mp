 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneActionRequest.java.
 // File size 507.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.controller.request;

import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName CsceneActionRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class CsceneActionRequest {
    @ApiModelProperty(value = "场景模板id")
    private String id;
    @ApiModelProperty(value = "场景上下文")
    private CsceneContextBO context;
}
