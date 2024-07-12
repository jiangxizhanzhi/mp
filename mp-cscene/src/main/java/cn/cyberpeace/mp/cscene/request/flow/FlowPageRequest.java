 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowPageRequest.java.
 // File size 951.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.request.flow;

import cn.cyberpeace.mp.cscene.request.common.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName FlowPageRequest
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/11/22 17:40
 * @Version 1.0.0
 * @see PageRequest
 */
@Data
public class FlowPageRequest extends PageRequest {

    @ApiModelProperty(value = "源主机")
    private String srcHost;

    @ApiModelProperty(value = "目的主机")
    private String destHost;

    @ApiModelProperty(value = "流量类型")
    private String flowType;

    @ApiModelProperty(value = "告警级别 1:低风险 2:中风险 3:高风险")
    private Integer riskRank;

    @ApiModelProperty(value = "开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endTime;

}
