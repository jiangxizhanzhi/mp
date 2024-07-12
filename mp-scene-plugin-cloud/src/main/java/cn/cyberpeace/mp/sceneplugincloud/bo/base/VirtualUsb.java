 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VirtualUsb.java.
 // File size 416.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName VirtualUsb
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class VirtualUsb {
    @ApiModelProperty(value = "usb id")
    private String uuid;

    @ApiModelProperty(value = "usb名称")
    private String name;
}

