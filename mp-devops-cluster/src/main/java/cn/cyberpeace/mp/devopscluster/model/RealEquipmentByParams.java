package cn.cyberpeace.mp.devopscluster.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName RealEquipmentByParams
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RealEquipmentByParams implements Serializable {

    @ApiModelProperty("交换机端口")
    private String switchPort;

    @ApiModelProperty("串口")
    private String ccmPort;

    @ApiModelProperty("UUID")
    private String deviceUuid;

    @ApiModelProperty("设备名称")
    private String deviceName;

    @ApiModelProperty("图标")
    private String logo;

    @ApiModelProperty("mac地址")
    private String mac;
}
