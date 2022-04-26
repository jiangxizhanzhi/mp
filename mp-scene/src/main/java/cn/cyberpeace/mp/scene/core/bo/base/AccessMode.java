package cn.cyberpeace.mp.scene.core.bo.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class AccessMode {

    @ApiModelProperty(value = "协议")
    private String protocol;

    @ApiModelProperty(value = "端口")
    private Integer port;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "是否开启ssh秘钥登录")
    private Boolean secretEnable;

    @ApiModelProperty(value = "ssh秘钥")
    private String secretPath;

    @ApiModelProperty(value = "路径")
    private String path;

    @ApiModelProperty(value = "描述")
    private String desc;

    @ApiModelProperty(value = "基础协议tcp/udp")
    private String baseProtocol;

    @ApiModelProperty(value = "开启代理")
    private Boolean proxy;

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

        public static final Map<String, Integer> DEFAULT_PORT_MAP = Map.of(
                SSH, 22,
                TELNET, 23,
                RDP, 3389,
                VNC, 5900,
                NC, 9999,
                HTTP, 80,
                HTTPS, 443
        );

        private Protocol() {}
    }

    public static class BaseProtocol {
        public static final String TCP = "tcp";
        public static final String UDP = "udp";

        private BaseProtocol() {}
    }
}
