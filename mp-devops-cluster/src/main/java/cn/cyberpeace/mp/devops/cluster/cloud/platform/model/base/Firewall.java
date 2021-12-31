package cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 防火墙信息
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Firewall {

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * ID
     */
    private String id;

    /**
     * 出口防火墙策略
     */
    private FirewallPolicy egressFirewallPolicy;

    /**
     * 入口防火墙策略
     */
    private FirewallPolicy ingressFirewallPolicy;

    /**
     * 端口列表
     */
    private List<String> ports;

    /**
     * 状态
     */
    private String status;

    /**
     *
     */
    private Boolean adminStateUp;
}
