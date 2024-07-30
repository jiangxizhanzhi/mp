 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SnapshotDeleteRequest.java.
 // File size 1133.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName SnapshotDeleteRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnapshotDeleteRequest {

    /**
     * 靶标id
     */
    @ApiModelProperty(value = "靶标id", required = true)
    @NotBlank
    private String deviceUuid;

    /**
     * 快照id
     */
    @ApiModelProperty(value = "快照id", required = true)
    @NotBlank
    private String snapshotUuid;

    /**
     * 快照名称
     */
    @ApiModelProperty(value = "快照名称", required = true)
    @NotBlank
    private String snapshotName;

    @ApiModelProperty(value = "实例id")
    private String instanceId;

    @Deprecated
    @ApiModelProperty(value = "快照id")
    private String snapshotId;

    @ApiModelProperty(value = "镜像类型")
    private String imageType;

}
