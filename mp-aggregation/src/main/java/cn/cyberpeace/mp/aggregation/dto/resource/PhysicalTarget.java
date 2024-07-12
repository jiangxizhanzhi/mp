 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name PhysicalTarget.java.
 // File size 706.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.dto.resource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @ClassName PhysicalTarget
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/9/13 11:06
 * @Version 1.0.0
 */
@Data
@Builder
@ApiModel("资源统计-实体靶标")
public class PhysicalTarget {

    @ApiModelProperty("应用系统")
    private Integer application;

    @ApiModelProperty("网络设备")
    private Integer network;

    @ApiModelProperty("操作系统")
    private Integer operateSystem;

    @ApiModelProperty("安全设备")
    private Integer security;

}
