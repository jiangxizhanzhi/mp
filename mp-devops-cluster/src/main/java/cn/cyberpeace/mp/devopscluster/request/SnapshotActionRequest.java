 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SnapshotActionRequest.java.
 // File size 620.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName SnapshotActionRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class SnapshotActionRequest {
    @ApiModelProperty(value = "实例id")
    private String instanceId;

    @ApiModelProperty(value = "快照id")
    private String snapshotId;

    @ApiModelProperty(value = "快照操作 回滚: rollback")
    private String action;

    @ApiModelProperty(value = "镜像类型")
    private String imageType;
}
