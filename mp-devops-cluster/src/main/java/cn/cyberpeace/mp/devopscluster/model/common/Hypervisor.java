package cn.cyberpeace.mp.devopscluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hypervisor implements Serializable {

    
    private String id;

    
    private String hostIp;

    
    private String hostname;

    
    private String type;

    
    private Integer cpu;

    
    private Long ram;

    
    private Long disk;

    
    private Integer cpuUsed;

    
    private Long ramUsed;

    
    private Long diskUsed;

    
    private Integer runningVms;

    
    private List<Server> servers;

    
    private String status;

    
    private String state;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Server implements Serializable{

        
        private String uuid;

        
        private String name;
    }
}

