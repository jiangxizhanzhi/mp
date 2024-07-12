 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name Firewall.java.
 // File size 1050.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName Firewall
 * @Description 防火墙信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Firewall implements Serializable {

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
