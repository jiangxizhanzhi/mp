package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author: guquan
 * @Date：2023/4/21 10:15
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
     * usb控制器device
     */
    @ApiModelProperty("usb控制器device")
    private Integer usbDevice;

}
