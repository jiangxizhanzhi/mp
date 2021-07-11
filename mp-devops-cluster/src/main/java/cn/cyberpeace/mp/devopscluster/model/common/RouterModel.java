package cn.cyberpeace.mp.devopscluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouterModel implements Serializable {

    
    private String id;

    
    private String name;

    
    private String description;

    
    private ExternalGateway externalGateway;

    
    private List<Routes> routes;

    
    private List<Interfaces> interfaces;

    
    private String status;

    
    private String project;

    
    private String createdAt;
    
    private String updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ExternalGateway implements Serializable{

        
        private String networkId;

        
        private String fixedIp;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Interfaces implements Serializable{

        
        private String networkId;

        
        private String fixedIp;

        
        private String portId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Routes implements Serializable{

        
        private String destination;

        
        private String nexthop;
    }
}
