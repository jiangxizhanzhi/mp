package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.Data;

import java.util.List;

@Data
public class VirtualNic {
    private String networkId;
    private String fixedIp;
    private QosRule qosRule;
    private Boolean enableExternal;
    private List<AccessMode> accessModes;
    private String firewallId;

    @Data
    public static class QosRule {
        private Integer maxKbps;
        private Integer maxBurstKbps;
        private String direction;
        private Integer lossPercent;
        private Integer delayMs;
        private Integer delayCorrelationMs;
    }

    @Data
    public static class AccessMode {
        private String protocol;
        private String ipAddress;
        private Integer port;
        private String username;
        private String password;
        private Boolean enablePortMapping;
        private String portMappingProtocol;
    }
}
