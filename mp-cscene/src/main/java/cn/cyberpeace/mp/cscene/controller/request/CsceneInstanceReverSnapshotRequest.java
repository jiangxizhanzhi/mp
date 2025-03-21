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
public class CsceneInstanceReverSnapshotRequest {
    @ApiModelProperty(value = "场景模板id")
    private String csceneId;
    @ApiModelProperty(value = "快照id")
    private String snapshotId;
}
