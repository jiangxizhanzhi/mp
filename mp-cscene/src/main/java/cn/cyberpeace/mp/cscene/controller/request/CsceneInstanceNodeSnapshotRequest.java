 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneInstanceNodeSnapshotRequest.java.
 // File size 564.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName CsceneInstanceNodeSnapshotRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CsceneInstanceNodeActionRequest
 */
@Data
public class CsceneInstanceNodeSnapshotRequest extends CsceneInstanceNodeActionRequest {
    @ApiModelProperty(value = "快照名称")
    private String snapshotName;
    @ApiModelProperty(value = "快照描述")
    private String description;
}
