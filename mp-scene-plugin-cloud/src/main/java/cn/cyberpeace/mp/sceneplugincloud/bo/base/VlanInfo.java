package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @BelongsPackage: cn.cyberpeace.mp.sceneplugincloud.bo.base
 * @Author: hhuang
 * @CreateTime: 2025/03/19  14:26
 * @Description: vlan信息
 * @Version: 1.0
 */
@Data
@ApiModel("vlan信息")
public class VlanInfo {

    @ApiModelProperty("vlanId")
    private Integer vlanId;

    @ApiModelProperty("cidr地址")
    private String cidr;

    @ApiModelProperty("网关")
    private String gateway;

    @ApiModelProperty("vlan下关联的端口")
    private List<VlanPortBO> ports;
}
