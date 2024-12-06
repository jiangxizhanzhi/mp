// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name FlowInfoQueryParams.java.
// File size 1648.
// File create date Fri Jul 12 12:55:23 CST 2024.
// File update date Fri Jul 12 12:55:23 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.flow.request.out;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName FlowInfoQueryParams
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see QueryParams
 */
@Data
@ApiModel(value = "流量通用查询条件")
public class OutputFlowInfoQueryParams {

    @ApiModelProperty(value = "分页页码")
    private Integer page;
    /**
     * 分页每页条数
     */
    @ApiModelProperty(value = "分页每页条数")
    private Integer pageSize;
    /**
     * 交互类型：0 背景流量，1 攻击流量，2 全流量
     */
    @ApiModelProperty(value = "交互类型：0 背景流量，1 攻击流量")
    private Integer interactiveType;

    @ApiModelProperty(value = "根据name模糊查询")
    private String name;

}
