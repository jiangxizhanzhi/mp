package cn.cyberpeace.mp.devopscluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NetworkAgentModel implements Serializable {

    
    private String id;

    
    private String agentType;

    
    private String binary;

    
    private String topic;

    
    private String host;

    
    private Boolean adminStateUp;

    
    private String createdAt;

    
    private String startedAt;

    
    private String heartbeatTimestamp;

    
    private String description;

    
    private Object resourcesSynced;

    
    private String availabilityZone;

    
    private Boolean alive;

    private Object configurations;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Configurations{

        private String dhcpDriver;

        private Long dhcpLeaseDuration;

        private Boolean logAgentHeartbeats;

        private Integer networks;

        private Integer ports;

        private Integer subnets;
    }
}
