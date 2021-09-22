package cn.cyberpeace.mp.devops.cluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirewallPolicy {

    private String id;

    private String name;

    private String description;

    private List<FirewallPolicyRule> rules;
}
