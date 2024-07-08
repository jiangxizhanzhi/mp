package cn.cyberpeace.mp.devopscluster.cloud.platform.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
/**
 * 虚拟节点类，用于表示和管理虚拟机或容器等虚拟资源。
 * 包含虚拟节点的各种属性，如ID、名称、主机名、状态等。
 * 还包括网络接口（Nic）、磁盘（Disk）、访问模式（AccessMode）等详细信息。
 */
public class VirtualNode {
    // 虚拟节点ID
    private String id;
    // 虚拟节点名称
    private String name;
    // 主机名
    private String host;
    // 节点状态
    private String status;
    // 创建时间
    private String createAt;
    // 网络接口列表
    private List<Nic> nics;
    // CPU数量
    private Integer cpu;
    // 内存大小
    private Integer memory;
    // 内存使用率历史列表
    private List<Integer> memoryUsageList;
    // CPU使用率历史列表
    private List<Integer> cpuUsageList;
    // 磁盘列表
    private List<Disk> disk;
    // 模板ID
    private String templateId;
    // 操作系统类型
    private String osType;
    // 当前内存使用率
    private Integer memoryUsage;
    // 当前网络使用率
    private Integer networkUsage;
    // 当前CPU使用率
    private Integer cpuUsage;
    // 当前磁盘使用率
    private Integer diskUsage;
    // 显示类型，如图形界面等
    private String displayType;
    // 运行时间
    private Double runningTime;
    // IP地址列表
    private String ips;
    // 启动磁盘大小
    private Integer bootableDiskSize;
    // 规格类型，如小型、中型、大型等
    private String flavor;
    // 节点类型，如虚拟机、容器等
    private String nodeType;
    // 显示状态，如运行中、停止等
    private String displayStatus;
    // 节点类型，可能与nodeType有所区别，更具体的类型
    private String type;

    // 描述信息
    private String description;
    // 图片链接或标识
    private String image;
    // 备注信息
    private String comment;
    // 日志路径
    private String logPath;

    // 场景名称，用于关联到特定的使用场景
    private String sceneName;
    // 是否启用virtio控制台
    private Boolean virtioConsole;

    // 访问模式列表，如SSH、RDP等
    private List<AccessMode> accessModes;

    /**
     * 网络接口类，描述虚拟节点的网络属性。
     */
    @Data
    public static class Nic {
        // 网络ID
        private String networkId;
        // MAC地址
        private String mac;
        // CIDR地址段
        private String cidr;
        // 网关地址
        private String gateway;
        // 端口名称
        private String portName;
        // 接口名称
        private String interfaceName;

        // 接口名称，可能与interfaceName相同或不同
        private String name;
        // 接口ID
        private String id;
        // 链接状态
        private Boolean linkStatus;
        // 接口类型
        private String type;
        // MAC地址
        private String macAddress;
        // 网络名称
        private String networkName;
        // IPv4地址列表
        private List<String> ipv4;
        // 是否启用PACP
        private Boolean pacpRunning;
    }

    /**
     * 磁盘类，描述虚拟节点的磁盘属性。
     */
    @Data
    public static class Disk {
        // 磁盘ID
        private String id;
        // 磁盘名称
        private String name;
        // 磁盘大小
        private Integer size;
        // 描述信息
        private String description;
        // 磁盘状态
        private String status;
        // 是否可启动
        private Boolean bootable;
        // 接口类型
        @JsonProperty(value = "interface")
        private String interface1;
        // 磁盘类型
        private String type;
        // 是否可共享
        private Boolean shareable;
        // 是否在删除节点时一起删除
        private Boolean deleteOnTermination;
    }
}

/**
 * 访问模式类，描述虚拟节点的各种访问方式及其属性。
 * 包括协议、端口、用户名、密码等信息。
 */
@Data
class AccessMode {

    // 协议类型
    @ApiModelProperty(value = "协议")
    private String protocol;

    // 端口号
    @ApiModelProperty(value = "端口")
    private Integer port;

    // 用户名
    @ApiModelProperty(value = "用户名")
    private String username;

    // 密码
    @ApiModelProperty(value = "密码")
    private String password;

    // 是否启用SSH密钥登录
    @ApiModelProperty(value = "是否开启ssh秘钥登录")
    private Boolean privateKeyEnable;

    // SSH密钥文件路径
    @ApiModelProperty(value = "ssh秘钥文件地址")
    private String privateKeyPath;

    // SSH密钥文件名
    @ApiModelProperty(value = "ssh秘钥文件名")
    private String privateKeyFileName;

    // 访问路径
    @ApiModelProperty(value = "路径")
    private String path;

    // 描述信息
    @ApiModelProperty(value = "描述")
    private String desc;

    // 基础协议，如TCP、UDP
    @ApiModelProperty(value = "基础协议tcp/udp")
    private String baseProtocol;

    // 是否启用代理
    @ApiModelProperty(value = "开启代理")
    private Boolean proxy;

    // 是否启用外网代理
    @ApiModelProperty(value = "开启外网代理")
    private Boolean publicNetProxy;

    // 外网代理IP
    @ApiModelProperty(value = "外网代理ip")
    private String publicNetProxyIp;

    // 外网代理端口
    @ApiModelProperty(value = "外网代理端口")
    private Integer publicNetProxyPort;

    // 是否为管理接入
    @ApiModelProperty(value = "是否是管理接入")
    private Boolean manageMode;

    /**
     * 访问模式的协议常量类，定义了各种访问协议及其默认端口。
     */
    public static class Protocol {
        // Ping协议常量，用于网络探测。
        public static final String PING = "ping";
        // 控制台协议常量，用于直接访问设备控制台。
        public static final String CONSOLE = "console";
        // Secure Shell (SSH)协议常量，用于安全远程登录。
        public static final String SSH = "ssh";
        // Telnet协议常量，用于远程登录。
        public static final String TELNET = "telnet";
        // Remote Desktop Protocol (RDP)协议常量，用于远程桌面连接。
        public static final String RDP = "rdp";
        // Virtual Network Computing (VNC)协议常量，用于远程桌面共享。
        public static final String VNC = "vnc";
        // Netcat (nc)协议常量，用于网络工具和协议分析。
        public static final String NC = "nc";
        // Hypertext Transfer Protocol (HTTP)协议常量，用于网页传输。
        public static final String HTTP = "http";
        // Hypertext Transfer Protocol Secure (HTTPS)协议常量，用于安全网页传输。
        public static final String HTTPS = "https";
        // 串行通信协议常量，用于串口通信。
        public static final String SERIAL = "serial";
        // COM端口常量，用于标识计算机上的串行端口。
        public static final String COM = "COM";

        // Simple Network Management Protocol (SNMP)协议常量，用于网络管理。
        public static final String SNMP = "snmp";

        public static final Map<String, Integer> DEFAULT_PORT_MAP = Map.of(
                SSH, 22,
                TELNET, 23,
                RDP, 3389,
                VNC, 5900,
                NC, 9999,
                HTTP, 80,
                HTTPS, 443,
                SNMP,161
        );

        private Protocol() {}
    }

    /**
     * 访问模式的基础协议常量类，定义了基础协议类型，如TCP、UDP。
     */
    public static class BaseProtocol {
        public static final String TCP = "tcp";
        public static final String UDP = "udp";

        private BaseProtocol() {}
    }
}
