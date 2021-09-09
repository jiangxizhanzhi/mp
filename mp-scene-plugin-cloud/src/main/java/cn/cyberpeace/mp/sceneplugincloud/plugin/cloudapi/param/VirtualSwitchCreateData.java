package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.HttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualSwitchCreateData implements HttpData {
    private String name;
    private Boolean entityNetwork;
    private Boolean configSwitch;
    private Integer segmentationId;
    private Subnet subnet;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Subnet {
        private Integer ipVersion;
        private String cidr;
        private String gatewayIp;
        private Boolean enableDhcp;
        private List<AllocationPool> allocationPools;
        private List<String> dnsNameservers;
        private List<HostRoute> hostRoutes;
        private List<String> switchVlanif;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class AllocationPool {
            private String start;
            private String end;
        }

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class HostRoute {
            private String destination;
            private String nexthop;
        }
    }
}
