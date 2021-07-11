package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirewallPolicyRule implements Serializable {

    
    private String id;

    
    private String name;

    
    private String description;

    
    private Object protocol;

    
    private Object firewallPolicyId;

    
    private Integer ipVersion;

    
    private Object sourceIpAddress;

    
    private Object destinationIpAddress;

    
    private Object sourcePort;

    
    private Object destinationPort;

    
    private String action;

    
    private Boolean enabled;

    
    private Boolean shared;
}
