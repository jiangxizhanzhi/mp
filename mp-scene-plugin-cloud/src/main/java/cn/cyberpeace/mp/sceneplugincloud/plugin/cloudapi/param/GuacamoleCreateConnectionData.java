 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name GuacamoleCreateConnectionData.java.
 // File size 1100.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @ClassName GuacamoleCreateConnectionData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuacamoleCreateConnectionData implements CloudHttpData {
    private String username;
    private String department;
    private String scene;
    private Boolean flag;
    private String host;
    private ConnectionParams connectionParams;

    private String vmId;
    private String privateKeyPath;

    @Data
    @NoArgsConstructor
    public static class ConnectionParams {
        private String protocol = "ssh";
        private String hostname;
        private String port;
        private String username;
        private String password;
        private Boolean needPrivateKey;
        private String command;
    }
}
