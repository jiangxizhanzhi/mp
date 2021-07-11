package cn.cyberpeace.mp.devopscluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class NetworkModel implements Serializable {

    
    private String id;

    
    private String name;

    
    private String description;

    
    private String networkType;

    
    private Integer mtu;

    
    private Integer segmentationId;

    
    private Integer portLimit;

    
    private Boolean portSecurityEnabled;

    
    private String bridgeName;

    
    private String hostname;

    
    private String mechanismDriver;

    
    private Subnet subnet;

    
    private String qosPolicyId;

    
    private String status;

    
    private String project;


    
    private Integer nodeNum;

    
    private Long inBps;

    
    private Long outBps;

    
    private String createdAt;
    
    private String updatedAt;



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Subnet implements Serializable{

        private String id;

        private String name;

        
        private String cidr;

        
        private Integer ipVersion;

        
        private String gatewayIp;

        
        private Boolean enableDhcp;

        
        private List<AllocationPools> allocationPools;

        
        private List<Object> dnsNameservers;

        
        private List<Object> hostRoutes;

        
        private Integer usedIps;

        
        private Integer freeIps;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static final class AllocationPools{

            private String start;

            private String end;
        }
    }
}
