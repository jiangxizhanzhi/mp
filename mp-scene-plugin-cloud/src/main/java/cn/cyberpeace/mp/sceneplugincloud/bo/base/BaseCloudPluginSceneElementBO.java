package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.validation.stereotype.VlanId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Data
public abstract class BaseCloudPluginSceneElementBO<M extends BaseCloudPluginSceneElementModel,
        T extends BaseCloudPluginSceneElementBO<M, T, S>, S extends BaseSceneElementBO<?, ?, S>>
        extends BasePluginSceneElementBO<M, T, S> {
    @ApiModelProperty(value = "是否外部引用资源")
    private Boolean external;
    @ApiModelProperty(value = "云平台虚拟化类型")
    private Integer virtualType;
    @ApiModelProperty(value = "是否透明模式")
    private Boolean transparent;
    @ApiModelProperty(value = "是否云平台內置靶標")
    private Boolean isBuildIn;
    @ApiModelProperty(value = "网络创建模式,0或空表示默认OpenStack网络,1表示macvlan网络")
    private Integer netMode;

    @ApiModelProperty(value = "云平台虚拟化资源id")
    private String resourceId;
    @ApiModelProperty(value = "云平台虚拟化资源实例id（载荷实例id）")
    private String loadResourceInstanceId;
    @ApiModelProperty(value = "载荷任务名称")
    private String loadTaskName;
    @ApiModelProperty(value = "载荷任务描述")
    private String loadTaskDescribe;
    @ApiModelProperty(value = "载荷访问工具控制页面执行的命令，vmware平台不需要")
    private String loadExecCommand;
    @ApiModelProperty(value = "载荷工具id")
    private String loadToolId;
    @ApiModelProperty(value = "载荷来源(1载荷平台 2本地)")
    private Integer loadSource;
    @DefaultStr
    private String loadAccessModel;
    @ApiModelProperty(value = "云平台三层防火墙资源id")
    private String firewallResourceId;

    // 虚拟交换机/路由/防火墙属性
    @ApiModelProperty(value = "电口数量")
    private Integer electricPortNumber;
    @ApiModelProperty(value = "电口类型")
    private String electricPortType;
    @ApiModelProperty(value = "光口数量")
    private Integer opticalPortNumber;
    @ApiModelProperty(value = "光口类型")
    private String opticalPortType;
    @ApiModelProperty(value = "wan口数量")
    private Integer wanPortNumber;
    @ApiModelProperty(value = "wan口类型")
    private String wanPortType;
    @ApiModelProperty(value = "lan口数量")
    private Integer lanPortNumber;
    @ApiModelProperty(value = "lan口类型")
    private String lanPortType;

    // 虚拟交换机属性
    @ApiModelProperty(value = "虚拟交换机cidr地址段")
    private String cidr;
    @ApiModelProperty(value = "虚拟交换机网关")
    private String gateway;
    @ApiModelProperty(value = "关闭虚拟交换机网关的分配")
    private Boolean gatewayOff;
    @ApiModelProperty(value = "虚拟交换机dns")
    private List<String> dnsNameservers;
    @ApiModelProperty(value = "虚拟交换机是否开启dhcp")
    private Boolean dhcp;
    @ApiModelProperty(value = "虚拟交换机地址池")
    @Valid
    private List<AllocationPool> allocationPools;
    @ApiModelProperty(value = "虚拟交换机静态路由")
    @Valid
    private List<HostRoute> hostRoutes;
    @ApiModelProperty(value = "虚拟交换机vlan模式")
    private Boolean vlan;
    @ApiModelProperty(value = "虚拟交换机vlanId")
    @VlanId
    private Integer vlanId;

    // 虚拟路由属性
    @ApiModelProperty(value = "虚拟路由静态路由")
    @Valid
    private List<StaticRoute> staticRoutes;

    // ospf网络属性
    @ApiModelProperty(value = "ospf网络下的routerID")
    private String ospfRouterId;

    @ApiModelProperty(value = "rip网络下的lookback")
    private String ripLoopback;

    // 虚拟防火墙属性
    @ApiModelProperty(value = "虚拟防火墙规则")
    @Valid
    private List<FirewallRule> firewallRules;

    // 虚拟机属性
    @ApiModelProperty(value = "挂载iso文件名")
    private String isoAttachFilename;
    @ApiModelProperty(value = "挂载iso临时文件地址")
    private String isoTmpdirUuid;

    // 虚拟机/容器属性
    @ApiModelProperty(value = "虚拟机/容器镜像名称")
    private String image;
    @ApiModelProperty(value = "虚拟机/容器镜像id")
    private String imageUuid;
    @ApiModelProperty(value = "终端初始包含工具")
    @Valid
    private List<InitialTool> initialTools;
    @ApiModelProperty(value = "仿真usb列表")
    private List<VirtualUsb> virtualUsbs;
    @ApiModelProperty(value = "实体usb列表")
    private List<EntityUsb> entityUsbs;
    @ApiModelProperty(value = "docker参数")
    private DockerParams dockerParams;

    //    Azure虚拟机镜像属性
    @ApiModelProperty(value = "虚拟机类型（linux/windows）")
    private String vmType;
    @ApiModelProperty(value = "虚拟机大小")
    private String vmSizeType;
    @ApiModelProperty(value = "虚拟机系统磁盘类型")//vm_os_disk_type
    private String vmOsDiskType;
    @ApiModelProperty(value = "发布者信息")
    private String publisher;
    @ApiModelProperty(value = "产品报价")
    private String offer;
    @ApiModelProperty(value = "产品SKU（Stock Keeping Unit）编码")
    private String sku;
    @ApiModelProperty(value = "源类型(1:云平台 2:用户上传)")
    private Integer sourceType;
    @ApiModelProperty(value = "用户上传映像id")
    private String uploadImageId;
    @ApiModelProperty(value = "用户名")
    private String username;
    @ApiModelProperty(value = "密码")
    private String password;
    @ApiModelProperty(value = "默认启动镜像名称")
    private String defaultLaunchImageName;
    @ApiModelProperty(value = "公共ip标记 0否 1是")
    private Boolean isPublicIp;
    @ApiModelProperty(value = "公共ip")
    private String publicIpAddress;
    @ApiModelProperty(value = "VNet网段信息")
    private List<String> cidrPools;
    @ApiModelProperty(value = "management_ip")
    private String managementIp;

    //    边界设备
    @ApiModelProperty(value = "边界设备-ip地址")
    private String address;
    @ApiModelProperty(value = "边界设备-接入协议")
    private String protocol;
    @ApiModelProperty(value = "边界设备-端口")
    private String port;

    //    漏洞
    @ApiModelProperty(value = "漏洞")
    private String loopholeInfo;
    @ApiModelProperty(value = "虚拟机扩容磁盘类型")
    private String vmExpansionDiskType;
    @ApiModelProperty(value = "是否扩容")
    private Boolean expansion;

    // gns3 属性
    @ApiModelProperty(value = "gns3 platform")
    private String platform;

    @ApiModelProperty(value = "snmp协议配置")
    private SnmpConfig snmpConfig;

    @ApiModelProperty(value = "mib文件数据")
    private List<MibData> mibData;

    // 靶标属性
    @ApiModelProperty(value = "靶标角色")
    private Integer role;
    @ApiModelProperty(value = "镜像类型")
    private String imageType;
    @ApiModelProperty(value = "靶标分类")
    private Integer category;
    @ApiModelProperty(value = "靶标二级分类")
    private Integer category2;
    @ApiModelProperty(value = "靶标三级分类")
    private Integer category3;
    @ApiModelProperty(value = "是否支持初始化")
    private Boolean initSupport;
    @ApiModelProperty(value = "是否支持快速创建")
    private Boolean quickCreateSupport;
    @ApiModelProperty(value = "快速创建参数")
    @Valid
    private List<QuickCreateParam> quickCreateParams;

    @ApiModelProperty(value = "是否开启辅助网络", hidden = true)
    @JsonIgnore
    @Deprecated
    private Boolean auxiliaryNetworkOn;
    @ApiModelProperty(value = "辅助网络id", hidden = true)
    @JsonIgnore
    @Deprecated
    private String auxiliaryNetworkId;
    @ApiModelProperty(value = "辅助网络cidr", hidden = true)
    @JsonIgnore
    @Deprecated
    private String auxiliaryNetworkCidr;
    @ApiModelProperty(value = "辅助网络网关", hidden = true)
    @JsonIgnore
    @Deprecated
    private String auxiliaryNetworkGateway;
    @ApiModelProperty(value = "辅助网络dns", hidden = true)
    @JsonIgnore
    @Deprecated
    private List<String> auxiliaryNetworkDnsNameservers;
    @ApiModelProperty(value = "辅助网络dhcp", hidden = true)
    @JsonIgnore
    @Deprecated
    private Boolean auxiliaryNetworkDhcp;
    @ApiModelProperty(value = "辅助网络地址池", hidden = true)
    @JsonIgnore
    @Valid
    @Deprecated
    private List<AllocationPool> auxiliaryNetworkAllocationPools;
    @ApiModelProperty(value = "辅助网络静态路由", hidden = true)
    @JsonIgnore
    @Valid
    @Deprecated
    private List<HostRoute> auxiliaryNetworkHostRoutes;
    @ApiModelProperty(value = "辅助网络vlan模式", hidden = true)
    @JsonIgnore
    @Deprecated
    private Boolean auxiliaryNetworkVlan;
    @ApiModelProperty(value = "辅助网络vlanId", hidden = true)
    @JsonIgnore
    @VlanId
    @Deprecated
    private Integer auxiliaryNetworkVlanId;
    @ApiModelProperty(value = "是否开启辅助网络辅助路由", hidden = true)
    @JsonIgnore
    private Boolean auxiliaryNetworkRouterOn;
    @ApiModelProperty(value = "辅助网络辅助路由id", hidden = true)
    @JsonIgnore
    private String auxiliaryNetworkRouterResourceId;
    @ApiModelProperty(value = "辅助网络辅助路由ip", hidden = true)
    @JsonIgnore
    private String auxiliaryNetworkRouterIp;
    @ApiModelProperty(value = "关闭辅助路由")
    private Boolean auxiliaryRouterOff;

    @ApiModelProperty(value = "是否开启管理网卡", hidden = true)
    @JsonIgnore
    private Boolean enableMgmt;
    @ApiModelProperty(value = "管理网卡端口资源id", hidden = true)
    @JsonIgnore
    private String mgmtPortResourceId;
    @ApiModelProperty(value = "管理网卡ip", hidden = true)
    @JsonIgnore
    private String mgmtIp;
    @ApiModelProperty(value = "管理网卡mac地址", hidden = true)
    @JsonIgnore
    private String mgmtMacAddr;

    @ApiModelProperty(value = "宿主机名称")
    private String host;

    //云场景-前端用
    @ApiModelProperty(value = "二级类型-云场景")
    private String serviceType;
    @ApiModelProperty(value = "三级类型-云场景")
    private String subType;
    @ApiModelProperty(value = "云服务类型-云场景")
    private String cloudServiceType;

    @Override
    public void fromModel(M obj, String... ignoreFields) {
        //忽略直接赋值处理的字段，需要定制处理
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getCidrPools),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getDnsNameservers),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getAllocationPools),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getStaticRoutes),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getFirewallRules),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getInitialTools),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getQuickCreateParams),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getAuxiliaryNetworkDnsNameservers),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getVirtualUsbs),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getEntityUsbs),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getDockerParams),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getAuxiliaryNetworkAllocationPools),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getSnmpConfig),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getMibData)
        ));
        cidrPools = StringUtils.isEmpty(obj.getCidrPools()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getCidrPools(), new TypeReference<List<String>>() {
        });
        dnsNameservers = StringUtils.isEmpty(obj.getDnsNameservers()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getDnsNameservers(), new TypeReference<List<String>>() {
        });
        allocationPools = StringUtils.isEmpty(obj.getAllocationPools()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getAllocationPools(), new TypeReference<List<AllocationPool>>() {
        });
        hostRoutes = StringUtils.isEmpty(obj.getHostRoutes()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getHostRoutes(), new TypeReference<List<HostRoute>>() {
        });
        staticRoutes = StringUtils.isEmpty(obj.getStaticRoutes()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getStaticRoutes(), new TypeReference<List<StaticRoute>>() {
        });
        firewallRules = StringUtils.isEmpty(obj.getFirewallRules()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getFirewallRules(), new TypeReference<List<FirewallRule>>() {
        });
        initialTools = StringUtils.isEmpty(obj.getInitialTools()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getInitialTools(), new TypeReference<List<InitialTool>>() {
        });
        quickCreateParams = StringUtils.isEmpty(obj.getQuickCreateParams()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getQuickCreateParams(), new TypeReference<List<QuickCreateParam>>() {
        });
        auxiliaryNetworkDnsNameservers = StringUtils.isEmpty(obj.getAuxiliaryNetworkDnsNameservers()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getAuxiliaryNetworkDnsNameservers(), new TypeReference<List<String>>() {
        });
        auxiliaryNetworkAllocationPools = StringUtils.isEmpty(obj.getAuxiliaryNetworkAllocationPools()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getAuxiliaryNetworkAllocationPools(), new TypeReference<List<AllocationPool>>() {
        });
        auxiliaryNetworkHostRoutes = StringUtils.isEmpty(obj.getAuxiliaryNetworkHostRoutes()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getAuxiliaryNetworkHostRoutes(), new TypeReference<List<HostRoute>>() {
        });
        virtualUsbs = StringUtils.isEmpty(obj.getVirtualUsbs()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getVirtualUsbs(), new TypeReference<List<VirtualUsb>>() {
        });
        entityUsbs = StringUtils.isEmpty(obj.getEntityUsbs()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getEntityUsbs(), new TypeReference<List<EntityUsb>>() {
        });
        dockerParams = StringUtils.isEmpty(obj.getDockerParams()) ? new DockerParams()
                : JsonCommon.parse(obj.getDockerParams(), new TypeReference<DockerParams>() {
        });
        snmpConfig = StringUtils.isEmpty(obj.getSnmpConfig()) ? new SnmpConfig()
                : JsonCommon.parse(obj.getSnmpConfig(), new TypeReference<SnmpConfig>() {
        });
        mibData = StringUtils.isEmpty(obj.getMibData()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getMibData(), new TypeReference<List<MibData>>() {
        });
    }

    @Override
    public M toModel(String... ignoreFields) {
        var obj = super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getCidrPools),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getDnsNameservers),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getAllocationPools),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getStaticRoutes),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getFirewallRules),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getInitialTools),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getQuickCreateParams),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getAuxiliaryNetworkDnsNameservers),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getVirtualUsbs),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getEntityUsbs),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getAuxiliaryNetworkAllocationPools),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getSnmpConfig),
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getMibData)
        ));
        if (cidrPools != null) {
            obj.setCidrPools(JsonCommon.stringify(cidrPools));
        }
        if (dnsNameservers != null) {
            obj.setDnsNameservers(JsonCommon.stringify(dnsNameservers));
        }
        if (hostRoutes != null) {
            obj.setHostRoutes(JsonCommon.stringify(hostRoutes));
        }
        if (allocationPools != null) {
            obj.setAllocationPools(JsonCommon.stringify(allocationPools));
        }
        if (staticRoutes != null) {
            obj.setStaticRoutes(JsonCommon.stringify(staticRoutes));
        }
        if (firewallRules != null) {
            obj.setFirewallRules(JsonCommon.stringify(firewallRules));
        }
        if (initialTools != null) {
            obj.setInitialTools(JsonCommon.stringify(initialTools));
        }
        if (quickCreateParams != null) {
            obj.setQuickCreateParams(JsonCommon.stringify(quickCreateParams));
        }
        if (auxiliaryNetworkDnsNameservers != null) {
            obj.setAuxiliaryNetworkDnsNameservers(JsonCommon.stringify(auxiliaryNetworkDnsNameservers));
        }
        if (auxiliaryNetworkAllocationPools != null) {
            obj.setAuxiliaryNetworkAllocationPools(JsonCommon.stringify(auxiliaryNetworkAllocationPools));
        }
        if (auxiliaryNetworkHostRoutes != null) {
            obj.setAuxiliaryNetworkHostRoutes(JsonCommon.stringify(auxiliaryNetworkHostRoutes));
        }
        if (virtualUsbs != null) {
            obj.setVirtualUsbs(JsonCommon.stringify(virtualUsbs));
        }
        if (entityUsbs != null) {
            obj.setEntityUsbs(JsonCommon.stringify(entityUsbs));
        }
        if (dockerParams != null) {
            obj.setDockerParams(JsonCommon.stringify(dockerParams));
        }
        if (snmpConfig != null) {
            obj.setSnmpConfig(JsonCommon.stringify(snmpConfig));
        }
        if (mibData != null) {
            obj.setMibData(JsonCommon.stringify(mibData));
        }
        return obj;
    }
}
