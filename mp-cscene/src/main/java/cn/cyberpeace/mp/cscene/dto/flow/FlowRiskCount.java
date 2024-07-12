 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowRiskCount.java.
 // File size 509.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.dto.flow;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName FlowRiskCount
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/11/22 17:58
 * @Version 1.0.0
 */
@Data
public class FlowRiskCount {

    @ApiModelProperty(notes = "高风险")
    private int highCount;

    @ApiModelProperty(notes = "中风险")
    private int mediumCount;

    @ApiModelProperty(notes = "中风险")
    private int lowCount;

}
