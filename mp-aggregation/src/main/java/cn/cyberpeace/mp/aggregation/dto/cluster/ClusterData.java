 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ClusterData.java.
 // File size 684.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.dto.cluster;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @ClassName ClusterData
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/9/13 13:58
 * @Version 1.0.0
 */
@Data
@Builder
@ApiModel("集群数据类型")
public class ClusterData {

    @ApiModelProperty(value = "cpu数据")
    private Integer[] CPUData;

    @ApiModelProperty("磁盘数据")
    private Integer[] DISKData;

    @ApiModelProperty("内存数据")
    private Integer[] RAMData;

    @ApiModelProperty("x轴")
    private String[] xAxis;

}
