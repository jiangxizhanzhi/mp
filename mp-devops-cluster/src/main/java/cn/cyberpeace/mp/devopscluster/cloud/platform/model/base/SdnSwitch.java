package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SdnSwitch {

    @ApiModelProperty("uuid")
    private String uuid;
    @ApiModelProperty("交换机名称")
    private String name;
    @ApiModelProperty("管理ip")
    private String managerIp;
    @ApiModelProperty("tunnelIp")
    private String tunnelIp;

    @ApiModelProperty("平台连接交换机自动获取")
    private Integer portNum;

    @ApiModelProperty("物理交换机实际标识")
    private String physicalSwitchName;
}
