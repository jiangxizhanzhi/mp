 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VirtualUsbQueryParams.java.
 // File size 623.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.request.queryparam;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName VirtualUsbQueryParams
 * @Description 虚拟USB通用查询条件
 * @Author guquan
 * @Date 2023/4/15 14:53
 * @Version 1.0.0
 * @see QueryParams
 */
@Data
@ApiModel("虚拟USB通用查询条件")
public class VirtualUsbQueryParams extends QueryParams {

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

}
