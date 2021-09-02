package cn.cyberpeace.mp.sceneplugincloud.model.base;

import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneElementModel;
import lombok.Data;

@Data
public class BaseCloudPluginSceneElementModel extends BasePluginSceneElementModel {
    /**
     * 外部资源
     */
    @DefaultBool
    private Boolean external;
    /**
     * 云平台资源类型
     */
    @DefaultInt(ResourceType.VIRTUAL_SWITCH)
    private Integer virtualType;

    /**
     * 云平台资源id
     */
    @DefaultStr
    private String resourceId;

    // 虚拟交换机/路由/防火墙属性
    @DefaultInt
    private Integer electricPortNumber;
    @DefaultStr(ElectricPortType.E)
    private String electricPortType;
    @DefaultInt
    private Integer opticalPortNumber;
    @DefaultStr(OpticalPortType.SFP)
    private String opticalPortType;

    // 虚拟交换机属性
    @DefaultStr
    private String cidr;
    @DefaultStr
    private String gateway;
    @DefaultStr
    private String dnsNameservers;
    @DefaultBool
    private Boolean dhcp;
    @DefaultInt
    private Integer vlanId;
    @DefaultStr
    private String allocationPools;

    // 虚拟路由属性
    @DefaultStr
    private String staticRoutes;

    // 虚拟防火墙属性
    @DefaultStr
    private String firewallIngressRules;
    @DefaultStr
    private String firewallEgressRules;

    // 虚拟机/容器属性
    @DefaultStr
    private String image;
    private String initialTools;

    public static class ResourceType {
        public static final int VIRTUAL_SWITCH = 1;
        public static final int VIRTUAL_ROUTER = 2;
        public static final int VIRTUAL_FIREWALL = 3;
        public static final int VIRTUAL_MACHINE = 4;
        public static final int CONTAINER = 5;
        public static final int ENTITY_NODE = 6;

        private ResourceType() {}
    }

    public static class ElectricPortType {
        public static final String E = "E";
        public static final String FE = "FE";
        public static final String GE = "GE";

        private ElectricPortType() {}
    }

    public static class OpticalPortType {
        public static final String SFP = "sfp";
        public static final String SFP_PLUS = "sfp+";

        private OpticalPortType() {}
    }

    /**
     * 用于设备连接的辅助网络
     */
    @DefaultStr
    private String auxiliaryNetworkId;
    /**
     * 用于设备连接的辅助网络
     */
    @DefaultStr
    private String auxiliaryNetworkCidr;
    /**
     * 用于设备连接的辅助网络
     */
    @DefaultStr
    private String auxiliaryNetworkGateway;
    /**
     * 用于设备连接的辅助网络
     */
    @DefaultStr
    private String auxiliaryNetworkDnsNameservers;
    /**
     * 用于设备连接的辅助网络
     */
    @DefaultBool(true)
    private Boolean auxiliaryNetworkDhcp;
    /**
     * 用于设备连接的辅助网络
     */
    @DefaultStr
    private String auxiliaryNetworkAllocationPools;

}
