package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementPortModel;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Data
public abstract class BaseCloudPluginSceneElementPortBO<M extends BaseCloudPluginSceneElementPortModel,
        T extends BaseCloudPluginSceneElementPortBO<M, T, S>, S extends BaseSceneElementPortBO<?, S, ?>>
        extends BasePluginSceneElementPortBO<M, T, S> {
    @ApiModelProperty(value = "云平台虚拟化资源id")
    private String resourceId;
    @ApiModelProperty(value = "是否nat外网")
    private Boolean nat;
    @ApiModelProperty(value = "虚拟防火墙规则")
    @Valid
    private List<FirewallRule> firewallRules;
    @ApiModelProperty(value = "是否是连接网络外网的端口")
    private Boolean connToExtNet = false;
    
    @ApiModelProperty(value = "实体设备端口接入类型")
    private String entityAccessType;
    @ApiModelProperty(value = "实体设备端口链接的交换机名称")
    private String entitySwitchName;
    @ApiModelProperty(value = "实体设备端口链接的交换机端口")
    private String entitySwitchPort;
    @ApiModelProperty(value = "实体设备端口号")
    private String entityPort;
    @ApiModelProperty(value = "网关设备端口类型")
    private Integer gatewayPortType;
    @ApiModelProperty(value = "网关设备端口地址段")
    private String gatewayPortIps;

    @ApiModelProperty(value = "辅助网络id")
    private String auxiliaryNetworkId;

    @ApiModelProperty(value = "qos id")
    private String qosId;
    @ApiModelProperty(value = "接入方式")
    @Valid
    private List<CloudAccessMode> accessModes;
    @ApiModelProperty(value = "弹性ip")
    private String floatingIp;
    @ApiModelProperty(value = "弹性ip的id")
    private String floatingIpId;
    @ApiModelProperty(value = "云平台三层防火墙资源id")
    private String firewallResourceId;
    @ApiModelProperty(value = "连接模式: dot1q-tunnel(单臂路由)")
    private String connectMode;

    
    @ApiModelProperty(value = "ospf组网下的ip地址")
    private String ospfIp;
    @ApiModelProperty(value = "ospf组网下的area")
    private String ospfArea;

    
    @ApiModelProperty(value = "bgp组网下的as")
    private String bgpAs;
    @ApiModelProperty(value = "bgp组网下的ip地址")
    private String bgpIp;
    @ApiModelProperty(value = "bgp组网下的对端端口ip")
    private String bgpPeer;
    @ApiModelProperty(value = "bgp组网下的对端端口所属AS")
    private String bgpPeerAs;

    
    @ApiModelProperty(value = "rip组网下的IP")
    private String ripIp;
    @ApiModelProperty(value = "ip下的邻居")
    private String ripPear;
    @Override
    public void fromModel(M obj, String... ignoreFields) {
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementPortModel::getFirewallRules),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementPortModel::getAccessModes)));
        firewallRules = StringUtils.isEmpty(obj.getFirewallRules()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getFirewallRules(), new TypeReference<List<FirewallRule>>(){});
        accessModes = StringUtils.isEmpty(obj.getAccessModes()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getAccessModes(), new TypeReference<List<CloudAccessMode>>(){});
    }

    @Override
    public M toModel(String... ignoreFields) {
        var obj = super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementPortModel::getFirewallRules),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementPortModel::getAccessModes)));
        if (firewallRules != null) {
            obj.setFirewallRules(JsonCommon.stringify(firewallRules));
        }
        if (accessModes != null) {
            obj.setAccessModes(JsonCommon.stringify(accessModes));
        }
        return obj;
    }
}
