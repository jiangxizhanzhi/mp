package cn.cyberpeace.mp.devopscluster.cloud.platform.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class VirtualNode {
    private String id;
    private String name;
    private String host;
    private String status;
    private String createAt;
    private List<Nic> nics;
    private Integer cpu;
    private Integer memory;
    private List<Integer> memoryUsageList;
    private List<Integer> cpuUsageList;
    private List<Disk> disk;
    private String templateId;
    private String osType;
    private Integer memoryUsage;
    private Integer networkUsage;
    private Integer cpuUsage;
    private Integer diskUsage;
    private String displayType;
    private Double runningTime;
    private String ips;
    private Integer bootableDiskSize;
    private String flavor;
    private String nodeType;
    private String displayStatus;
    private String type;

    private String description;
    private String image;
    private String comment;
    private String logPath;

    private String sceneName;
    private Boolean virtioConsole;

    private List<AccessMode> accessModes;

    @Data
    public static class Nic {
        private String networkId;
        private String mac;
        private String cidr;
        private String gateway;
        private String portName;
        private String interfaceName;

        private String name;
        private String id;
        private Boolean linkStatus;
        private String type;
        private String macAddress;
        private String networkName;
        private List<String> ipv4;
        private Boolean pacpRunning;
    }

    @Data
    public static class Disk {
        private String id;
        private String name;
        private Integer size;
        private String description;
        private String status;
        private Boolean bootable;
        @JsonProperty(value = "interface")
        private String interface1;
        private String type;
        private Boolean shareable;
        private Boolean deleteOnTermination;
    }
}

@Data
class AccessMode {

    @ApiModelProperty(value = "协议")
    private String protocol;

    @ApiModelProperty(value = "端口")
    private Integer port;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "是否开启ssh秘钥登录")
    private Boolean privateKeyEnable;

    @ApiModelProperty(value = "ssh秘钥文件地址")
    private String privateKeyPath;

    @ApiModelProperty(value = "ssh秘钥文件名")
    private String privateKeyFileName;

    @ApiModelProperty(value = "路径")
    private String path;

    @ApiModelProperty(value = "描述")
    private String desc;

    @ApiModelProperty(value = "基础协议tcp/udp")
    private String baseProtocol;

    @ApiModelProperty(value = "开启代理")
    private Boolean proxy;

    @ApiModelProperty(value = "开启外网代理")
    private Boolean publicNetProxy;

    @ApiModelProperty(value = "外网代理ip")
    private String publicNetProxyIp;

    @ApiModelProperty(value = "外网代理端口")
    private Integer publicNetProxyPort;

    @ApiModelProperty(value = "是否是管理接入")
    private Boolean manageMode;

    public static class Protocol {
        public static final String PING = "ping";
        public static final String CONSOLE = "console";
        public static final String SSH = "ssh";
        public static final String TELNET = "telnet";
        public static final String RDP = "rdp";
        public static final String VNC = "vnc";
        public static final String NC = "nc";
        public static final String HTTP = "http";
        public static final String HTTPS = "https";
        public static final String SERIAL = "serial";
        public static final String COM = "COM";

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

    public static class BaseProtocol {
        public static final String TCP = "tcp";
        public static final String UDP = "udp";

        private BaseProtocol() {}
    }
}
