package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Firewall implements Serializable {

    
    private String name;

    
    private String description;

    
    private String id;

    
    private FirewallPolicy egressFirewallPolicy;

    
    private FirewallPolicy ingressFirewallPolicy;

    
    private List<String> ports;

    
    private String status;

    
    private Boolean adminStateUp;
}
