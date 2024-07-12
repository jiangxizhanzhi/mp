 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneInstanceNodesActionRequest.java.
 // File size 503.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName CsceneInstanceNodesActionRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CsceneInstanceActionRequest
 */
@Data
public class CsceneInstanceNodesActionRequest extends CsceneInstanceActionRequest {
    @ApiModelProperty(value = "节点id列表")
    private List<String> nodeIds;
}
