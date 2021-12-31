package cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirewallPolicyRule {

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
     * 协议
     */
    private Object protocol;

    /**
     * 防火墙策略ID
     */
    private Object firewallPolicyId;

    /**
     * IP版本
     */
    private Integer ipVersion;

    /**
     * 源IP地址
     */
    private Object sourceIpAddress;

    /**
     * 目的IP地址
     */
    private Object destinationIpAddress;

    /**
     * 源端口
     */
    private Object sourcePort;

    /**
     * 目的端口
     */
    private Object destinationPort;

    /**
     * 操作
     */
    private String action;

    /**
     * 开启
     */
    private Boolean enabled;

    /**
     * 共享
     */
    private Boolean shared;
}
