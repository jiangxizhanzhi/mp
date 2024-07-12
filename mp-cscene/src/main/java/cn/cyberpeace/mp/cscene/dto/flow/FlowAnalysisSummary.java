 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowAnalysisSummary.java.
 // File size 752.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.dto.flow;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @ClassName FlowAnalysisSummary
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/3/16 09:55
 * @Version 1.0.0
 */
@Data
@Builder
public class FlowAnalysisSummary {

    @ApiModelProperty(value = "总流量(GB)")
    private Double totalFlowSize;

    @ApiModelProperty(value = "攻击流量(GB)")
    private Double totalAttackFlowSize;

    @ApiModelProperty(value = "网络连接")
    private Integer netConnSize;

    @ApiModelProperty(value = "带宽占用")
    private Double bandwith;

    @ApiModelProperty(value = "攻击事件")
    private Long attackCount;

}
