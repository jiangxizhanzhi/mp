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

