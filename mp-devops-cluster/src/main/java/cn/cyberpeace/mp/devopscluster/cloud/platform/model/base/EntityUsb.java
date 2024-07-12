 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name EntityUsb.java.
 // File size 1308.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName EntityUsb
 * @Description 实体USB
 * @Author guquan
 * @Date 2023/4/21 10:15
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
@ApiModel("实体USB")
public class EntityUsb extends BaseModel {

    /**
     * 主机名
     */
    @ApiModelProperty("主机名")
    private String host;

    /**
     * 实体usb信息, 服务器上lsusb显示的U盘信息
     */
    @ApiModelProperty("实体usb信息, 服务器上lsusb显示的U盘信息")
    private String usbInfo;

    /**
     * usb控制器总线
     */
    @ApiModelProperty("usb控制器总线")
    private Integer usbBus;

    /**
     * usb控制器port
     */
    @ApiModelProperty("usb控制器port")
    private Integer usbPort;

    /**
     * usb控制器device
     */
    @ApiModelProperty("usb控制器device")
    private Integer usbDevice;

    /**
     * 型号
     */
    @ApiModelProperty("型号")
    private String style;

    /**
     * 参数
     */
    @ApiModelProperty("参数")
    private String params;

}
