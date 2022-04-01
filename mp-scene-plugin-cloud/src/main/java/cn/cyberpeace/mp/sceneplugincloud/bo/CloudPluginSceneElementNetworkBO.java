package cn.cyberpeace.mp.sceneplugincloud.bo;

import cn.cyberpeace.mp.base.bo.ModelBO;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.AllocationPool;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.HostRoute;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementNetworkModel;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Description
 * @Author maojun
 * @Date 2022/3/9
 */
@Getter
@Setter
@NoArgsConstructor
public class CloudPluginSceneElementNetworkBO extends ModelBO<CloudPluginSceneElementNetworkModel, CloudPluginSceneElementNetworkBO> {
    private String id;
    private String scene;
    private String sceneElement;
    @ApiModelProperty(value = "云平台虚拟化资源id")
    private String resourceId;
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
    @ApiModelProperty(value = "是否外部网络")
    private Boolean external;
    @ApiModelProperty(value = "辅助网络辅助路由是否开启", hidden = true)
    private Boolean auxiliaryNetworkRouterOn;
    @ApiModelProperty(value = "辅助网络辅助路由id", hidden = true)
    private String auxiliaryNetworkRouterResourceId;
    @ApiModelProperty(value = "辅助网络辅助路由ip", hidden = true)
    private String auxiliaryNetworkRouterIp;

    private List<String> ipPool = new ArrayList<>();
    private Set<String> excloudIps = new HashSet<>();

    public CloudPluginSceneElementNetworkBO(String scene, String sceneElement) {
        this.id = TextUtils.uuid();
        this.scene = scene;
        this.sceneElement = sceneElement;
    }

    @Override
    public CloudPluginSceneElementNetworkModel createModel() {
        return new CloudPluginSceneElementNetworkModel();
    }

    @Override
    public void fromModel(CloudPluginSceneElementNetworkModel obj, String... ignoreFields) {
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(CloudPluginSceneElementNetworkModel::getDnsNameservers),
                BeanUtils.getBeanGetterFieldName(CloudPluginSceneElementNetworkModel::getAllocationPools),
                BeanUtils.getBeanGetterFieldName(CloudPluginSceneElementNetworkModel::getHostRoutes))
        );
        dnsNameservers = StringUtils.isEmpty(obj.getDnsNameservers()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getDnsNameservers(), new TypeReference<List<String>>() {
        });
        allocationPools = StringUtils.isEmpty(obj.getAllocationPools()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getAllocationPools(), new TypeReference<List<AllocationPool>>() {
        });
        hostRoutes = StringUtils.isEmpty(obj.getHostRoutes()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getHostRoutes(), new TypeReference<List<HostRoute>>() {
        });
    }

    @Override
    public CloudPluginSceneElementNetworkModel toModel(String... ignoreFields) {
        var obj = super.toModel(
                ArrayUtils.addAll(ignoreFields,
                        BeanUtils.getBeanGetterFieldName(CloudPluginSceneElementNetworkModel::getDnsNameservers),
                        BeanUtils.getBeanGetterFieldName(CloudPluginSceneElementNetworkModel::getAllocationPools),
                        BeanUtils.getBeanGetterFieldName(CloudPluginSceneElementNetworkModel::getHostRoutes))
        );
        if (this.dnsNameservers != null) {
            obj.setDnsNameservers(JsonCommon.stringify(this.dnsNameservers));
        }
        if (this.allocationPools != null) {
            obj.setAllocationPools(JsonCommon.stringify(this.allocationPools));
        }
        if (this.hostRoutes != null) {
            obj.setHostRoutes(JsonCommon.stringify(this.hostRoutes));
        }
        return obj;
    }
}