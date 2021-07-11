package cn.cyberpeace.mp.devopscluster.converter.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VirtualRealSwitchBO {

    
    private String name;

    
    private String managementIp;

    
    private String state;

    
    private Integer ports;

    
    private Integer usedPorts;
}
