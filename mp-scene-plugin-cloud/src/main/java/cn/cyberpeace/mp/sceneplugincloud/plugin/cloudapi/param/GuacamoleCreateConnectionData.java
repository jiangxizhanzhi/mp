package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuacamoleCreateConnectionData implements CloudHttpData {
    private String username;
    private String department;
    private String scene;
    private Boolean flag;
    private ConnectionParams connectionParams;

    @Data
    @NoArgsConstructor
    public static class ConnectionParams {
        private Boolean disableGlyphCaching;
        private String protocol;
        private String hostname;
        private String port;
        private String username;
        private String password;
        private String privateKeyPath;
    }
}
