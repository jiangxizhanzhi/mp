 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VirtualFirewallCreateData.java.
 // File size 1344.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.bo.base.FirewallRule;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName VirtualFirewallCreateData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualFirewallCreateData implements CloudHttpData {
    private String name;
    private List<VirtualRouterCreateData.StaticRoute> routeStatics;
    private List<VirtualRouterNic> networks;
    private List<VirtualNic> network;

    private List<Rule> ingressRules;
    private List<Rule> egressRules;
    private List<FirewallRule> rules;
    private List<String> ports;

    private String id;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Rule {
        private String sourceIpAddress;
        private String destinationIpAddress;
        private String sourcePort;
        private String destinationPort;
        private Integer ipVersion;
        private String protocol;
        private String action;

        private String name;
    }
}
