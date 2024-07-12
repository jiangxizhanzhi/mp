 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TargetSummary.java.
 // File size 792.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.dto.resource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @ClassName TargetSummary
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/9/13 11:06
 * @Version 1.0.0
 */
@Data
@Builder
@ApiModel("资源统计-靶标类型统计")
public class TargetSummary {

    @ApiModelProperty("容器靶标")
    private Integer containerTarget;

    @ApiModelProperty("实体靶标")
    private Integer physicalTarget;

    @ApiModelProperty("tgm靶标")
    private Integer tgmTarget;

    @ApiModelProperty("靶场测试")
    private Integer cyberTestScene;

    @ApiModelProperty("虚拟靶标")
    private Integer vmTarget;

}
