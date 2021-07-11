package cn.cyberpeace.mp.devopscluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstanceOsServiceModel implements Serializable {

    
    private String id;

    
    private String binary;

    
    private String host;

    
    private String zone;

    
    private String status;

    
    private String state;

    
    private String disabledReason;

    
    private String updatedAt;
}
