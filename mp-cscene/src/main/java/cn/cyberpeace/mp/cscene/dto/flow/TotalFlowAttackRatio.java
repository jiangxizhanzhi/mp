 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TotalFlowAttackRatio.java.
 // File size 874.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.dto.flow;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName TotalFlowAttackRatio
 * @Description
 * @Author maojun
 * @Date 2022/2/22
 * @Version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalFlowAttackRatio {

    /**
     * 攻击次数
     */
    @ApiModelProperty(value = "攻击次数")
    private Long totalAttackNumber;

    /**
     * 流量大小，单位B
     */
    @ApiModelProperty(value = "流量大小，单位B")
    private Double totalTrafficSize;

    /**
     * 攻击手法列表
     */
    @ApiModelProperty(value = "攻击手法列表")
    private List<FlowAttackRatio> attackWayList;
}