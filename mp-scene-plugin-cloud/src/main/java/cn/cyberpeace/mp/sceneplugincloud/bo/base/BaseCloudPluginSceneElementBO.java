package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.base.util.JsonUtils;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class BaseCloudPluginSceneElementBO<M extends BaseCloudPluginSceneElementModel,
        T extends BaseCloudPluginSceneElementBO<M, T>> extends BasePluginSceneElementBO<M, T> {
    @ApiModelProperty(value = "是否外部引用资源")
    private Boolean external;
    @ApiModelProperty(value = "云平台虚拟化类型")
    private Integer virtualType;
    @ApiModelProperty(value = "是否透明模式")
    private Boolean transparent;

    @ApiModelProperty(value = "云平台虚拟化资源id")
    private String resourceId;

    // 虚拟交换机/路由/防火墙属性
    @ApiModelProperty(value = "电口数量")
    private Integer electricPortNumber;
    @ApiModelProperty(value = "电口类型")
    private String electricPortType;
    @ApiModelProperty(value = "光口数量")
    private Integer opticalPortNumber;
    @ApiModelProperty(value = "光口类型")
    private String opticalPortType;

    // 虚拟交换机属性
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
    @ApiModelProperty(value = "虚拟交换机vlan模式")
    private Boolean vlan;
    @ApiModelProperty(value = "虚拟交换机vlanId")
    private Integer vlanId;

    // 虚拟路由属性
    @ApiModelProperty(value = "虚拟路由静态路由")
    private List<StaticRoute> staticRoutes;

    // 虚拟防火墙属性
    @ApiModelProperty(value = "虚拟防火墙规则")
    private List<FirewallRule> firewallRules;

    // 虚拟机/容器属性
    @ApiModelProperty(value = "虚拟机/容器镜像名称")
    private String image;
    @ApiModelProperty(value = "终端初始包含工具")
    private List<InitialTool> initialTools;

    // 靶标属性
    @ApiModelProperty(value = "靶标角色")
    private Integer role;
    @ApiModelProperty(value = "镜像类型")
    private String imageType;
    @ApiModelProperty(value = "靶标分类")
    private Integer category;
    @ApiModelProperty(value = "是否支持初始化")
    private Boolean initSupport;
    @ApiModelProperty(value = "是否支持快速创建")
    private Boolean quickCreateSupport;
    @ApiModelProperty(value = "快速创建参数")
    private List<QuickCreateParam> quickCreateParams;

    @ApiModelProperty(value = "是否开启辅助网络", hidden = true)
    @JsonIgnore
    private Boolean auxiliaryNetworkOn;
    @ApiModelProperty(value = "辅助网络id", hidden = true)
    @JsonIgnore
    private String auxiliaryNetworkId;
    @ApiModelProperty(value = "辅助网络cidr", hidden = true)
    @JsonIgnore
    private String auxiliaryNetworkCidr;
    @ApiModelProperty(value = "辅助网络网关", hidden = true)
    @JsonIgnore
    private String auxiliaryNetworkGateway;
    @ApiModelProperty(value = "辅助网络dns", hidden = true)
    @JsonIgnore
    private List<String> auxiliaryNetworkDnsNameservers;
    @ApiModelProperty(value = "辅助网络dhcp", hidden = true)
    @JsonIgnore
    private Boolean auxiliaryNetworkDhcp;
    @ApiModelProperty(value = "辅助网络地址池", hidden = true)
    @JsonIgnore
    private List<AllocationPool> auxiliaryNetworkAllocationPools;
    @ApiModelProperty(value = "辅助网络vlan模式", hidden = true)
    @JsonIgnore
    private Boolean auxiliaryNetworkVlan;
    @ApiModelProperty(value = "辅助网络vlanId", hidden = true)
    @JsonIgnore
    private Integer auxiliaryNetworkVlanId;

    @Override
    public void fromModel(M obj, String... ignoreFields) {
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getDnsNameservers),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getAllocationPools),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getStaticRoutes),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getFirewallRules),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getInitialTools),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getQuickCreateParams),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getAuxiliaryNetworkDnsNameservers),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getAuxiliaryNetworkAllocationPools)));
        dnsNameservers = StringUtils.isEmpty(obj.getDnsNameservers()) ? new ArrayList<>()
                : JsonUtils.parse(obj.getDnsNameservers(), new TypeReference<List<String>>(){});
        allocationPools = StringUtils.isEmpty(obj.getAllocationPools()) ? new ArrayList<>()
                : JsonUtils.parse(obj.getAllocationPools(), new TypeReference<List<AllocationPool>>(){});
        staticRoutes = StringUtils.isEmpty(obj.getStaticRoutes()) ? new ArrayList<>()
                : JsonUtils.parse(obj.getStaticRoutes(), new TypeReference<List<StaticRoute>>(){});
        firewallRules = StringUtils.isEmpty(obj.getFirewallRules()) ? new ArrayList<>()
                : JsonUtils.parse(obj.getFirewallRules(), new TypeReference<List<FirewallRule>>(){});
        initialTools = StringUtils.isEmpty(obj.getInitialTools()) ? new ArrayList<>()
                : JsonUtils.parse(obj.getInitialTools(), new TypeReference<List<InitialTool>>(){});
        quickCreateParams = StringUtils.isEmpty(obj.getQuickCreateParams()) ? new ArrayList<>()
                : JsonUtils.parse(obj.getQuickCreateParams(), new TypeReference<List<QuickCreateParam>>(){});
        auxiliaryNetworkDnsNameservers = StringUtils.isEmpty(obj.getAuxiliaryNetworkDnsNameservers()) ? new ArrayList<>()
                : JsonUtils.parse(obj.getAuxiliaryNetworkDnsNameservers(), new TypeReference<List<String>>(){});
        auxiliaryNetworkAllocationPools = StringUtils.isEmpty(obj.getAuxiliaryNetworkAllocationPools()) ? new ArrayList<>()
                : JsonUtils.parse(obj.getAuxiliaryNetworkAllocationPools(), new TypeReference<List<AllocationPool>>(){});
    }

    @Override
    public M toModel(String... ignoreFields) {
        var obj = super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getDnsNameservers),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getAllocationPools),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getStaticRoutes),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getFirewallRules),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getInitialTools),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getQuickCreateParams),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getAuxiliaryNetworkDnsNameservers),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getAuxiliaryNetworkAllocationPools)));
        if (dnsNameservers != null) {
            obj.setDnsNameservers(JsonUtils.toJsonString(dnsNameservers));
        }
        if (allocationPools != null) {
            obj.setAllocationPools(JsonUtils.toJsonString(allocationPools));
        }
        if (staticRoutes != null) {
            obj.setStaticRoutes(JsonUtils.toJsonString(staticRoutes));
        }
        if (firewallRules != null) {
            obj.setFirewallRules(JsonUtils.toJsonString(firewallRules));
        }
        if (initialTools != null) {
            obj.setInitialTools(JsonUtils.toJsonString(initialTools));
        }
        if (quickCreateParams != null) {
            obj.setQuickCreateParams(JsonUtils.toJsonString(quickCreateParams));
        }
        if (auxiliaryNetworkDnsNameservers != null) {
            obj.setAuxiliaryNetworkDnsNameservers(JsonUtils.toJsonString(auxiliaryNetworkDnsNameservers));
        }
        if (auxiliaryNetworkAllocationPools != null) {
            obj.setAuxiliaryNetworkAllocationPools(JsonUtils.toJsonString(auxiliaryNetworkAllocationPools));
        }
        return obj;
    }
}
