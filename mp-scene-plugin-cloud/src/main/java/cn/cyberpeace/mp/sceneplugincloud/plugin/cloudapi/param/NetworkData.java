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

    /***********30**************/
    private String id;
    private String networkType;
    private String name;
    private String description;
    private String status;
    private Integer mtu;
    private String cidr;
    private String comment;
    private String createdAt;
    private Subnet subnet;

    /******************not 30*******************/


    private Boolean entityNetwork;
    private Boolean configSwitch;
    private Integer segmentationId;
    private String portLimit;
    private Boolean portSecurityEnabled;
    private String bridgeName;
    private String hostname;
    private String mechanismDriver;
    private String qosPolicyId;
    private String project;
    private String updatedAt;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Subnet {
        /**********30***********/

        private String name;
        private String cidr;
        private String gatewayIp;
        private List<String> dnsNameservers;



        /************not 30***********/
        private Integer ipVersion;
        private Boolean enableDhcp;
        private List<AllocationPool> allocationPools;
        private List<HostRoute> hostRoutes;
        private String id;

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
