package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.po.DeviceCloudTargetNet;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 网络-防火墙
 */
@Data
@ApiModel("网络-防火墙")
public class ExtFireWall extends DeviceCloudTargetNet {

    @ApiModelProperty("是否分配公网ip")
    private Boolean isPublicIp;

    @ApiModelProperty("防火墙策略")
    private FirewallPolicy firewallPolicy;

}
