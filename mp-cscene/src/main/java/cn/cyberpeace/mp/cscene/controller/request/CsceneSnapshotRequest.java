// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name CsceneInstanceActionRequest.java.
// File size 517.
// File create date Fri Jul 12 12:55:22 CST 2024.
// File update date Fri Jul 12 12:55:22 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.cscene.controller.request;

import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName CsceneInstanceActionRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class CsceneSnapshotRequest {
    @ApiModelProperty(value = "场景实例id")
    private String csceneInstanceId;
    @ApiModelProperty(value = "场景快照名称")
    private String snapshotName;
    @ApiModelProperty(value = "场景上下文")
    private CsceneContextBO context;
}
