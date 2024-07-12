 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ActivationResourceItem.java.
 // File size 722.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadmin.dto.activation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ActivationResourceItem
 * @Description 授权资源项
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 */
@Data
public class ActivationResourceItem {

    @ApiModelProperty(value = "资源项ID")
    private String id;

    @ApiModelProperty(value = "唯一编号")
    private String identifier;

    @ApiModelProperty(value = "产品/模块")
    private String module;

    @ApiModelProperty(value = "控制项名称")
    private String restrictName;

    @ApiModelProperty(value = "最大值")
    private String maxValue;

}
