 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowTrend.java.
 // File size 447.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.dto.flow;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName FlowTrend
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/12/1 17:21
 * @Version 1.0.0
 */
@Data
public class FlowTrend {

    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ApiModelProperty("流量大小")
    private Long dataSize;
}
