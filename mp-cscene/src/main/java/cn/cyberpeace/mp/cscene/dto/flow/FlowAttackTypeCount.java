 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowAttackTypeCount.java.
 // File size 535.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.dto.flow;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName FlowAttackTypeCount
 * @Description
 * @Author maojun
 * @Date 2022/2/22
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowAttackTypeCount {
    @ApiModelProperty("攻击类型")
    private String attackType;

    @ApiModelProperty("攻击次数")
    private Integer times;

}
