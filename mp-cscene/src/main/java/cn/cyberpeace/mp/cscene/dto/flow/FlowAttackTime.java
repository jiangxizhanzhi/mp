 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowAttackTime.java.
 // File size 602.
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
 * @ClassName FlowAttackTime
 * @Description
 * @Author maojun
 * @Date 2022/2/22
 * @Version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowAttackTime {
    @ApiModelProperty("节点名称")
    private String nodeName;

    @ApiModelProperty("ip")
    private String ip;

    @ApiModelProperty("次数")
    private Integer times;
}