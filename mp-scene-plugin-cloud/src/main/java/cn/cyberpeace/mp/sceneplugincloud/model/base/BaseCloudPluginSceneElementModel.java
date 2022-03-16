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
    @DefaultInt(VirtualType.VIRTUAL_SWITCH)
    private Integer virtualType;
    @DefaultBool
    private Boolean transparent;
    @DefaultBool
    private Boolean isBuildIn;

    /**
     * 云平台资源id
     */
    @DefaultStr
    private String resourceId;
    /**
     * 三层防火墙对应防火墙资源id
     */
    @DefaultStr
    private String firewallResourceId;

    // 虚拟交换机/路由/防火墙属性
    @DefaultInt
    private Integer electricPortNumber;
    @DefaultStr(ElectricPortType.E)
    private String electricPortType;
    @DefaultInt
    private Integer opticalPortNumber;
    @DefaultStr(OpticalPortType.SFP)
    private String opticalPortType;
    @DefaultInt
    private Integer wanPortNumber;
    @DefaultStr(WanPortType.E)
    private String wanPortType;
    @DefaultInt
    private Integer lanPortNumber;
    @DefaultStr(LanPortType.E)
    private String lanPortType;

    // 虚拟交换机属性
    @DefaultStr
    private String cidr;
    @DefaultStr
    private String gateway;
    @DefaultStr
    private String dnsNameservers;
    @DefaultBool(true)
    private Boolean dhcp;
    @DefaultStr
    private String allocationPools;
    @DefaultStr
    private String hostRoutes;
    @DefaultBool
    private Boolean vlan;
    @DefaultInt
    private Integer vlanId;

    // 虚拟路由属性
    @DefaultStr
    private String staticRoutes;

    // 虚拟防火墙属性
    @DefaultStr
    private String firewallRules;

    // 虚拟机/容器属性
    @DefaultStr
    private String image;
    @DefaultStr
    private String initialTools;

    // 靶标属性
    @DefaultInt
    private Integer role;
    @DefaultStr
    private String imageType;
    @DefaultInt
    private Integer category;
    @DefaultInt
    private Integer category2;
    @DefaultInt
    private Integer category3;
    @DefaultBool
    private Boolean initSupport;
    @DefaultBool
    private Boolean quickCreateSupport;
    @DefaultStr
    private String quickCreateParams;

    // 宿主机信息
    @DefaultStr
    private String host;


    public static class VirtualType {
        public static final int VIRTUAL_SWITCH = 1;
        public static final int VIRTUAL_ROUTER = 2;
        public static final int VIRTUAL_FIREWALL = 3;
        public static final int VIRTUAL_MACHINE = 4;
        public static final int CONTAINER = 5;
        public static final int ENTITY_NODE = 6;
        public static final int NPC = 7;

        private VirtualType() {
        }
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

    public static class WanPortType {
        public static final String E = "E";

        private WanPortType() {
        }
    }

    public static class LanPortType {
        public static final String E = "E";

        private LanPortType() {
        }
    }

    public static class Role {
        public static final int VIRTUAL_LAYER_2_GATEWAY = 1;
        public static final int VIRTUAL_LAYER_3_GATEWAY = 2;
        public static final int VIRTUAL_EXTERNAL_NETWORK = 3;
        public static final int VIRTUAL_TERMINAL = 4;
        public static final int VIRTUAL_TRANSPARENT_GATEWAY = 5;
        public static final int VIRTUAL_GATEWAY = 6;
        public static final int NPC = 7;
        public static final int VIRTUAL_LAYER_3_IMAGE_GATEWAY = 20;
        public static final int VIRTUAL_LAYER_3_SIMPLE_ROUTER = 21;
        public static final int VIRTUAL_LAYER_3_SIMPLE_SWITCH = 22;
        public static final int VIRTUAL_LAYER_3_SIMPLE_FIREWALL = 23;
        public static final int ENTITY_GATEWAY = 50;
        public static final int ENTITY_TERMINAL = 51;

        private Role() {}
    }

    public static class ImageType {
        public static final String SIMPLE = "simple";
        public static final String VM = "vm";
        public static final String CONTAINER = "container";
        public static final String ENTITY = "entity";

        private ImageType() {}
    }

    // 用于设备连接的辅助网络
    @DefaultBool
    private Boolean auxiliaryNetworkOn;
    @DefaultStr
    private String auxiliaryNetworkId;
    @DefaultStr
    private String auxiliaryNetworkCidr;
    @DefaultStr
    private String auxiliaryNetworkGateway;
    @DefaultStr
    private String auxiliaryNetworkDnsNameservers;
    @DefaultBool(true)
    private Boolean auxiliaryNetworkDhcp;
    @DefaultStr
    private String auxiliaryNetworkAllocationPools;
    @DefaultStr
    private String auxiliaryNetworkHostRoutes;
    @DefaultBool
    private Boolean auxiliaryNetworkVlan;
    @DefaultInt
    private Integer auxiliaryNetworkVlanId;
    /**
     * 辅助网络辅助路由，ovs流表需要网络必须有默认路由
     */
    @DefaultBool
    private Boolean auxiliaryNetworkRouterOn;
    @DefaultStr
    private String auxiliaryNetworkRouterResourceId;
    @DefaultStr
    private String auxiliaryNetworkRouterIp;

    // 用于管理网卡
    @DefaultBool
    private Boolean enableMgmt;
    @DefaultStr
    private String mgmtPortResourceId;
    @DefaultStr
    private String mgmtIp;
    @DefaultStr
    private String mgmtMacAddr;

}
