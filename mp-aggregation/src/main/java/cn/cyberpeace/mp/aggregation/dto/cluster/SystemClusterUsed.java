 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SystemClusterUsed.java.
 // File size 587.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.dto.cluster;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @ClassName SystemClusterUsed
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/9/13 13:56
 * @Version 1.0.0
 */
@Data
@Builder
@ApiModel("靶场应用运行统计")
public class SystemClusterUsed {

    @ApiModelProperty(value = "集群数据")
    private ClusterData clusterData;

    @ApiModelProperty(value = "系统数据")
    private SystemData systemData;

}
