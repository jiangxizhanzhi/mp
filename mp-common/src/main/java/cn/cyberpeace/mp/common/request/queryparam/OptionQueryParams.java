 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OptionQueryParams.java.
 // File size 644.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.common.request.queryparam;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 选项通用查询条件
 * @Author: guquan
 * @Date：2023/5/5 14:31
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

}
