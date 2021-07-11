package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirewallPolicy implements Serializable {

    
    private String id;

    
    private String name;

    
    private String description;

    
    private List<FirewallPolicyRule> rules;
}
