package cn.cyberpeace.mp.sceneplugincloud.bo;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.AllocationPool;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.HostRoute;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/3/9
 */
@Getter
@Setter
public class CloudPluginSceneElementNetworkBO {
    @ApiModelProperty(value = "云平台虚拟化资源id")
    private String resourceId;
    @ApiModelProperty(value = "辅助网络id")
    private String networkId;
    @ApiModelProperty(value = "虚拟交换机cidr地址段")
    private String cidr;
    @ApiModelProperty(value = "虚拟交换机网关")
    private String gateway;
    @ApiModelProperty(value = "虚拟交换机dns")
    private List<String> dnsNameservers;
    @ApiModelProperty(value = "虚拟交换机是否开启dhcp")
    private Boolean dhcp;
    @ApiModelProperty(value = "虚拟交换机地址池")
    private List<AllocationPool> allocationPools;
    @ApiModelProperty(value = "虚拟交换机静态路由")
    private List<HostRoute> hostRoutes;
    @ApiModelProperty(value = "虚拟交换机vlan模式")
    private Boolean vlan;
    @ApiModelProperty(value = "虚拟交换机vlanId")
    private Integer vlanId;

    private Boolean external;

    private String id = TextUtils.uuid();

    private List<String> ipPool = new ArrayList<>();

    private List<CloudPluginSceneElementPortBO> fromPorts = new ArrayList<>();

    private List<CloudPluginSceneElementPortBO> toPorts = new ArrayList<>();
}