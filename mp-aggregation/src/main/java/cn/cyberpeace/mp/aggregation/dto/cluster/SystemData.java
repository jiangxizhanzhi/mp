 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SystemData.java.
 // File size 668.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.dto.cluster;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @ClassName SystemData
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/9/13 13:58
 * @Version 1.0.0
 */

@Data
@Builder
@ApiModel("系统数据")
public class SystemData {

    @ApiModelProperty("cpu数据")
    private Integer[] CPUData;

    @ApiModelProperty("磁盘数据")
    private Integer[] DISKData;

    @ApiModelProperty("内存数据")
    private Integer[] RAMData;

    @ApiModelProperty("x轴")
    private String[] xAxis;
}
