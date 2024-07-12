 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SnapshotMergeRequest.java.
 // File size 1042.
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
 * @ClassName SnapshotMergeRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnapshotMergeRequest {

    /**
     * 镜像ID
     */
    @ApiModelProperty(value = "镜像ID",required = true)
    @NotBlank
    private String uuid;

    /**
     * 镜像类型
     */
    @ApiModelProperty(value = "镜像类型",required = true)
    @NotBlank
    private String imageType;

    /**
     * 最新一条的快照名称
     */
    @ApiModelProperty(value = "最新一条的快照名称",required = true)
    @NotBlank
    private String snapshotName;

    /**
     * 新的快照名称
     */
    @ApiModelProperty("新的快照名称")
    private String newName;
}
