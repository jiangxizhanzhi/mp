package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityNodeCreateData implements CloudHttpData {
    private String name;
    private String requestUser;
    private List<Interface> interfaces;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Interface {
        private String interfaceName;
        private String linkType;
        private Integer allowPass;
        private String networkId;
        private List<AccessMode> accessModes;

        public static class LinkType {
            public static final String ACCESS = "access";
            public static final String TRUNK = "trunk";

            private LinkType() {}
        }

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class AccessMode {
            private String protocol;
            private String ipAddress;
            private Integer port;
            private String username;
            private String password;
            private String macAddress;

            private Connection connection;
            private String security;

            @Data
            @AllArgsConstructor
            @NoArgsConstructor
            public static class Connection {
                private String url;
                private String connectionId;
                private String connectionName;
            }
        }
    }
}
