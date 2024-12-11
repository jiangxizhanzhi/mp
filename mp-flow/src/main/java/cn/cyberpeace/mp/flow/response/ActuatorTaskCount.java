// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name FlowActuatorResponse.java.
// File size 1882.
// File create date Fri Jul 12 12:55:31 CST 2024.
// File update date Fri Jul 12 12:55:31 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.flow.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @ClassName FlowActuatorResponse
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Getter
@Setter
@ApiModel(value = "流量发生器返回信息")
@Builder
public class ActuatorTaskCount {
    @ApiModelProperty(value = "类型")
    private String name;
    @ApiModelProperty(value = "数量")
    private Integer count;
}
