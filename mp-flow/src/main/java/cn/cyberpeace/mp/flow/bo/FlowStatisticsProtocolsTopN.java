// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name FlowStatisticsAll.java.
// File size 1138.
// File create date Fri Jul 12 12:55:32 CST 2024.
// File update date Fri Jul 12 12:55:32 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.flow.bo;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName FlowStatisticsAll
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@ApiModel("实时速率统计对象")
public class FlowStatisticsProtocolsTopN {

    @ApiModelProperty(value = "TOP5流量协议")
    private ArrayNode topProtocols;
}
