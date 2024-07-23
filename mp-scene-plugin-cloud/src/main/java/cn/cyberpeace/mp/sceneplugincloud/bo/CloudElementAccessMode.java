 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudElementAccessMode.java.
 // File size 980.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName CloudElementAccessMode
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class CloudElementAccessMode {
    @ApiModelProperty(value = "协议")
    private String protocol;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "连接url")
    private String connectionUrl;

    public static class Protocol {
        public static final String CONSOLE = "console";
        public static final String VNC = "vnc";
        public static final String NO_VNC = "novnc";
        public static final String SPICE = "spice";
        public static final String COM = "com";
        public static final String SSH = "ssh";
        public static final String RDP = "rdp";
        public static final String TELNET = "telnet";

        /**
         * Protocol
         */
        private Protocol() {}
    }
}