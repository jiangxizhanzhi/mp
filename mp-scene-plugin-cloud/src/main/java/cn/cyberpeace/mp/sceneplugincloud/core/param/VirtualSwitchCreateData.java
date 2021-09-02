package cn.cyberpeace.mp.sceneplugincloud.core.param;

import cn.cyberpeace.mp.sceneplugincloud.core.common.HttpData;
import lombok.Data;

import java.util.List;

@Data
public class VirtualSwitchCreateData implements HttpData {
    private String name;
    private Boolean entityNetwork;
    private Boolean configSwitch;
    private Integer segmentationId;
    private Subnet subnet;

    @Data
    public static class Subnet {
        private Integer ipVersion;
        private String cidr;
        private String gatewayIp;
        private String enableDhcp;
        private List<AllocationPool> allocationPools;
        private List<String> dnsNameservers;
        private List<HostRoute> hostRoutes;
        private List<String> switchVlanif;

        @Data
        public static class AllocationPool {
            private String start;
            private String end;
        }

        @Data
        public static class HostRoute {
            private String destination;
            private String nexthop;
        }
    }
}
