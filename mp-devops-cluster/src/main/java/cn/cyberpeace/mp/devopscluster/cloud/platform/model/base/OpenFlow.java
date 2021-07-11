package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class OpenFlow {

    
    private String actions;

    
    private String matchCondition;

    
    private String cookie;

    
    private String duration;

    
    private String table;

    
    private String nPackets;

    
    private String nBytes;

    
    private String idleAge;

    
    private String ovsName;

    
    private String hostname;

}
