package cn.cyberpeace.mp.sceneplugincollection.plugin.azure.functionrequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirewallPolicy implements Serializable {

    /**
     * ID
     */
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 防火墙策略规则列表
     */
    private List<FirewallPolicyRule> rules;
}