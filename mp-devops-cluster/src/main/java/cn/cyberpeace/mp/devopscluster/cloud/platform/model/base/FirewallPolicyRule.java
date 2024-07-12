 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FirewallPolicyRule.java.
 // File size 1349.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName FirewallPolicyRule
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirewallPolicyRule implements Serializable {

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
