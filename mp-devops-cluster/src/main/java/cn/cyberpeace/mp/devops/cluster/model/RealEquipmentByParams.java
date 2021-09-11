package cn.cyberpeace.mp.devops.cluster.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RealEquipmentByParams {

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
}
