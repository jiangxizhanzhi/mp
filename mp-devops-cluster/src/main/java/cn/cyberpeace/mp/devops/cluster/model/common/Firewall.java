package cn.cyberpeace.mp.devops.cluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Firewall {

    private String name;

    private String description;

    private String id;

    private String egressFirewallPolicyId;

    private String ingressFirewallPolicyId;

    private List<String> ports;

    private String status;

    private Boolean adminStateUp;
}
