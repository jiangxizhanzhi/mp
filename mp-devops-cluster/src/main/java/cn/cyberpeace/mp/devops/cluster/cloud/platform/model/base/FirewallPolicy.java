package cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 防火墙策略
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirewallPolicy {

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
