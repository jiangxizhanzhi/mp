// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name OptionQueryParams.java.
// File size 879.
// File create date Fri Jul 12 12:55:31 CST 2024.
// File update date Fri Jul 12 12:55:31 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.common.request.queryparam;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName OptionQueryParams
 * @Description 选项通用查询条件
 * @Author guquan
 * @Date 2023/5/5 14:31
 * @Version 1.0.0
 * @see QueryParams
 */
@Data
@ApiModel(value = "选项通用查询条件")
public class OptionQueryParams extends QueryParams {

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    private String type;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String zhName;

    @ApiModelProperty(value = "名称en")
    private String enName;

}
