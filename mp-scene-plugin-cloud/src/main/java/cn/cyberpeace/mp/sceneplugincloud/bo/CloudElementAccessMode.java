package cn.cyberpeace.mp.sceneplugincloud.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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

        private Protocol() {}
    }
}