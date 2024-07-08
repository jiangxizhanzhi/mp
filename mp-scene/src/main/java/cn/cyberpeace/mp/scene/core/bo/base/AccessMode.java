package cn.cyberpeace.mp.scene.core.bo.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName AccessMode
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
/**
 * 访问模式类，用于定义和存储各种访问相关的信息，如协议、端口和认证方式等。
 */
public class AccessMode {

    /**
     * 远程访问协议，例如SSH、TELNET等。
     */
    @ApiModelProperty(value = "协议")
    private String protocol;

    /**
     * 远程访问所使用的端口号。
     */
    @ApiModelProperty(value = "端口")
    private Integer port;

    /**
     * 登录远程服务的用户名。
     */
    @ApiModelProperty(value = "用户名")
    private String username;

    /**
     * 登录远程服务的密码。
     */
    @ApiModelProperty(value = "密码")
    private String password;

    /**
     * 是否启用SSH密钥认证。
     */
    @ApiModelProperty(value = "是否开启ssh秘钥登录")
    private Boolean privateKeyEnable;

    /**
     * SSH密钥文件的路径。
     */
    @ApiModelProperty(value = "ssh秘钥文件地址")
    private String privateKeyPath;

    /**
     * SSH密钥文件的名称。
     */
    @ApiModelProperty(value = "ssh秘钥文件名")
    private String privateKeyFileName;

    /**
     * 访问路径，可能用于某些特定协议的访问定位。
     */
    @ApiModelProperty(value = "路径")
    private String path;

    /**
     * 对访问模式的描述信息。
     */
    @ApiModelProperty(value = "描述")
    private String desc;

    /**
     * 基础协议，如tcp、udp等。
     */
    @ApiModelProperty(value = "基础协议tcp/udp")
    private String baseProtocol;

    /**
     * 是否开启代理访问。
     */
    @ApiModelProperty(value = "开启代理")
    private Boolean proxy;

    /**
     * 是否开启公网代理。
     */
    @ApiModelProperty(value = "开启外网代理")
    private Boolean publicNetProxy;

    /**
     * 公网代理的IP地址。
     */
    @ApiModelProperty(value = "外网代理ip")
    private String publicNetProxyIp;

    /**
     * 公网代理的端口号。
     */
    @ApiModelProperty(value = "外网代理端口")
    private Integer publicNetProxyPort;

    /**
     * 是否以管理模式访问，可能用于区分普通访问和管理访问。
     */
    @ApiModelProperty(value = "是否是管理接入")
    private Boolean manageMode;

    /**
     * 访问协议的内部类，定义了多种访问协议及其默认端口。
     */
    public static class Protocol {
        /**
         * 各种访问协议的常量定义。
         */
        // Ping协议常量，用于网络探测和故障排除。
        public static final String PING = "ping";
        // 控制台访问协议常量，用于直接访问设备或系统的命令行界面。
        public static final String CONSOLE = "console";
        // Secure Shell (SSH)协议常量，用于安全地远程访问和管理服务器。
        public static final String SSH = "ssh";
        // Telnet协议常量，一种用于远程访问和管理网络设备的协议。
        public static final String TELNET = "telnet";
        // Remote Desktop Protocol (RDP)协议常量，用于远程访问和控制Windows操作系统。
        public static final String RDP = "rdp";
        // Virtual Network Computing (VNC)协议常量，用于远程访问和控制图形界面。
        public static final String VNC = "vnc";
        // Netcat (nc)协议常量，一种用于网络连接和数据传输的工具。
        public static final String NC = "nc";
        // Hypertext Transfer Protocol (HTTP)协议常量，用于在Web浏览器和服务器之间传输数据。
        public static final String HTTP = "http";
        // Hypertext Transfer Protocol Secure (HTTPS)协议常量，用于在Web浏览器和服务器之间安全地传输数据。
        public static final String HTTPS = "https";
        // Serial Port (串口)协议常量，用于通过串行通信访问设备或系统。
        public static final String SERIAL = "serial";
        // COM端口常量，用于标识Windows系统的串行端口。
        public static final String COM = "COM";
        public static final String SNMP = "snmp";

        /**
         * 默认端口映射，用于存储各种协议的默认端口号。
         */
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

        /**
         * 避免实例化。
         */
        private Protocol() {}
    }

    /**
     * 基础协议的内部类，定义了tcp和udp两种基础协议。
     */
    public static class BaseProtocol {
        public static final String TCP = "tcp";
        public static final String UDP = "udp";

        /**
         * 避免实例化。
         */
        private BaseProtocol() {}
    }
}
