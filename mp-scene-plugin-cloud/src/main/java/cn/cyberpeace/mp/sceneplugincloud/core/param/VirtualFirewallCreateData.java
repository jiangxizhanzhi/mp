package cn.cyberpeace.mp.sceneplugincloud.core.param;

import cn.cyberpeace.mp.sceneplugincloud.core.common.HttpData;
import lombok.Data;

import java.util.List;

@Data
public class VirtualFirewallCreateData implements HttpData {
    private String name;
    private List<Rule> ingressRules;
    private List<Rule> egressRules;
    private List<String> ports;

    @Data
    public static class Rule {
        private String sourceIpAddress;
        private String destinationIpAddress;
        private String sourcePort;
        private String destinationPort;
        private Integer ipVersion;
        private String protocol;
        private String action;
    }
}
