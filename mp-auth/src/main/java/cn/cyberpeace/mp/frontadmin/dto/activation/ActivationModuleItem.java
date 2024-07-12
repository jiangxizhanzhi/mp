 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ActivationModuleItem.java.
 // File size 562.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadmin.dto.activation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ActivationModuleItem
 * @Description 授权功能项
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 */
@Data
public class ActivationModuleItem {

    @ApiModelProperty(value = "应用模块ID")
    private String id;

    @ApiModelProperty(value = "唯一编号")
    private String identifier;

    @ApiModelProperty(value = "应用模块名称")
    private String name;

}
