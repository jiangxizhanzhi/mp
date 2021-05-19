package cn.cyberpeace.mp.scene.core.bo.base;

import lombok.Data;

import java.util.Map;

@Data
public class AccessMode {
    private String protocol;
    private Integer port;
    private String username;
    private String password;
    private String path;
    private String desc;
    private String baseProtocol;

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
