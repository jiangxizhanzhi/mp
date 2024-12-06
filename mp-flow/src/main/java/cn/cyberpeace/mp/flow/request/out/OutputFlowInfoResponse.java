// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name FlowInfoResponse.java.
// File size 3359.
// File create date Fri Jul 12 12:55:30 CST 2024.
// File update date Fri Jul 12 12:55:30 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.flow.request.out;

import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.model.FlowScriptParamModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @ClassName FlowInfoResponse
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "流量返回信息")
public class OutputFlowInfoResponse {

    /**
     * 流量id
     */
    @ApiModelProperty(value = "流量id")
    private String resourceId;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    private String title;

    /**
     * 简述
     */
    @ApiModelProperty(value = "简述")
    private String introduction;

    /**
     * 简述(兼容)
     */
    @ApiModelProperty(value = "简述(兼容)")
    private String flowDesc;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private String createTime;

    /**
     * 交互类型：0 背景流量，1 攻击流量，2 全流量
     */
    @ApiModelProperty(value = "交互类型：0 背景流量，1 攻击流量")
    private Integer interactiveType;

    /**
     * 攻击脚本参数列表
     */
    @ApiModelProperty(value = "攻击流量 脚本运行参数")
    private List<FlowScriptParamModel> scriptParamModels;
}
