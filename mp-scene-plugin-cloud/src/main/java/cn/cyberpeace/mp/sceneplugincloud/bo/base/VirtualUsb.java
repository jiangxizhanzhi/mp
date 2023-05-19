package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class VirtualUsb {
    @ApiModelProperty(value = "usb id")
    private String uuid;

    @ApiModelProperty(value = "usb名称")
    private String name;
}

