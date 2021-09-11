package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualNic {
    private String networkId;
    private String fixedIp;
    private List<QosRule> qosRules;
    private Boolean enableExternal;
    private List<AccessMode> accessModes;
    private String firewallId;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QosRule {
        private Integer maxKbps;
        private Integer maxBurstKbps;
        private String direction;
        private Integer lossPercent;
        private Integer delayMs;
        private Integer delayCorrelationMs;
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
        private Boolean enablePortMapping;
        private String portMappingProtocol;
    }
}
