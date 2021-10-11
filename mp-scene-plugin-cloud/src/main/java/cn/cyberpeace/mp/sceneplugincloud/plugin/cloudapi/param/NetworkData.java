package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetworkData implements CloudHttpData {

    private String name;
    private Boolean entityNetwork;
    private Boolean configSwitch;
    private Integer segmentationId;
    private Subnet subnet;

    private String id;
    private String networkType;

    private String description;
    private Integer mtu;
    private String portLimit;
    private Boolean portSecurityEnabled;
    private String bridgeName;
    private String hostname;
    private String mechanismDriver;
    private String qosPolicyId;
    private String status;
    private String project;
    private String updatedAt;
    private String createdAt;

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

        private String id;
        private String name;

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
