 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SnapshotRequest.java.
 // File size 754.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName SnapshotRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnapshotRequest {

    /**
     * 虚拟机/容器ID
     */
    @ApiModelProperty(value = "虚拟机/容器ID")
    private String instanceUuid;

    /**
     * 镜像类型
     */
    @ApiModelProperty(value = "镜像类型")
    private String imageType;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;
}
