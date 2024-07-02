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
