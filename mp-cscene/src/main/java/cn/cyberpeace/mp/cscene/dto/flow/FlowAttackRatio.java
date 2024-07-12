 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowAttackRatio.java.
 // File size 671.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.dto.flow;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName FlowAttackRatio
 * @Description
 * @Author maojun
 * @Date 2022/2/24
 * @Version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowAttackRatio {
    /**
     * 攻击类型
     */
    @ApiModelProperty(value = "攻击类型")
    private String type;
    /**
     * 攻击类型占比
     */
    @ApiModelProperty(value = "攻击类型占比")
    private Double attackTrafficPercent;
}
