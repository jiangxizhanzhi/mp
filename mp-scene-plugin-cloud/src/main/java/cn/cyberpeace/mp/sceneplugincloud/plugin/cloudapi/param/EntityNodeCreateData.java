package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.HttpData;
import lombok.Data;

import java.util.List;

@Data
public class EntityNodeCreateData implements HttpData {
    private String name;
    private String requestUser;
    private List<Interface> interfaces;

    @Data
    public static class Interface {
        private String interfaceName;
        private String linkType;
        private Integer allowPass;
        private String networkId;
        private List<AccessMode> accessModes;

        public static class LinkType {
            public static final String ACCESS = "access";
            public static final String TRUNK = "trunk";
        }

        @Data
        public static class AccessMode {
            private String protocol;
            private String ipAddress;
            private Integer port;
            private String username;
            private String password;
            private String macAddress;
        }
    }
}
