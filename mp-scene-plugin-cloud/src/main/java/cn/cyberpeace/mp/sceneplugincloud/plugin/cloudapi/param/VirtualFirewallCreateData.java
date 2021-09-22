package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualFirewallCreateData implements CloudHttpData {
    private String name;
    private List<Rule> ingressRules;
    private List<Rule> egressRules;
    private List<String> ports;

    private String id;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
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
