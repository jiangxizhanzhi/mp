 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ContainerSnapshot.java.
 // File size 806.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ContainerSnapshot
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class ContainerSnapshot {
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "快照名称")
    private String name;
    @ApiModelProperty(value = "容器id")
    private String containerId;
    @ApiModelProperty(value = "快照id")
    private String imageId;
    @ApiModelProperty(value = "创建时间")
    private String createAt;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "状态")
    private String status;
}
