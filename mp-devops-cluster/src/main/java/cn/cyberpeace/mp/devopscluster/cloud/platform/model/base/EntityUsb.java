package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("实体USB")
public class EntityUsb extends BaseModel {

    
    @ApiModelProperty("主机名")
    private String host;

    
    @ApiModelProperty("实体usb信息, 服务器上lsusb显示的U盘信息")
    private String usbInfo;

    
    @ApiModelProperty("usb控制器总线")
    private Integer usbBus;

    
    @ApiModelProperty("usb控制器port")
    private Integer usbPort;

    
    @ApiModelProperty("usb控制器device")
    private Integer usbDevice;

    
    @ApiModelProperty("型号")
    private String style;

    
    @ApiModelProperty("参数")
    private String params;

}
