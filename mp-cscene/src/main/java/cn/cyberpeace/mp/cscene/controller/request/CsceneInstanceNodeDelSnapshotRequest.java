 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneInstanceNodeDelSnapshotRequest.java.
 // File size 480.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName CsceneInstanceNodeDelSnapshotRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CsceneInstanceNodeActionRequest
 */
@Data
public class CsceneInstanceNodeDelSnapshotRequest extends CsceneInstanceNodeActionRequest {
    @ApiModelProperty(value = "快照")
    private String snapshotId;
}