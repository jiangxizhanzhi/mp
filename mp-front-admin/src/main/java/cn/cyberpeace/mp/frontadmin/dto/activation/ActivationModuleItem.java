 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ActivationModuleItem.java.
 // File size 510.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.dto.activation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 授权功能项
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
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
