package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetworkAclData implements CloudHttpData {

    private static final String TO_LPORT = "to-lport";

    private String direction;
    private String action;
    private String match;
    private Integer priority;
    private String networkId;


    public static NetworkAclData getFirstAclConfig(String networkId) {
        NetworkAclData networkAclData = new NetworkAclData();
        networkAclData.setDirection(TO_LPORT);
        networkAclData.setAction("drop");
        networkAclData.setMatch("ip4 || ip6");
        networkAclData.setPriority(1001);
        networkAclData.setNetworkId(networkId);
        return networkAclData;
    }

    public static NetworkAclData getSecondAclConfig(String configAgentIp, String networkId) {
        NetworkAclData networkAclData = new NetworkAclData();
        networkAclData.setDirection(TO_LPORT);
        networkAclData.setAction("allow");
        networkAclData.setMatch(String.format("ip4.src == %s || ip4.dst == %s", configAgentIp, configAgentIp));
        networkAclData.setPriority(1002);
        networkAclData.setNetworkId(networkId);

        return networkAclData;
    }

    public static NetworkAclData getThirdAclConfig(String configAgentIp, String currentMpServerIp, String networkId) {
        NetworkAclData networkAclData = new NetworkAclData();
        networkAclData.setDirection(TO_LPORT);
        networkAclData.setAction("allow");
        networkAclData.setMatch(String.format("ip4.src == %s && ip4.dst == %s", configAgentIp, currentMpServerIp));
        networkAclData.setPriority(1003);
        networkAclData.setNetworkId(networkId);

        return networkAclData;
    }

    @Data
    public static class NetworkAclDto implements CloudHttpData{
        private String direction;
        private String action;
        private String match;
        private Integer priority;

        public NetworkAclDto(NetworkAclData networkAclData) {
            this.direction = networkAclData.getDirection();
            this.action = networkAclData.getAction();
            this.match = networkAclData.getMatch();
            this.priority = networkAclData.getPriority();
        }

    }
}
