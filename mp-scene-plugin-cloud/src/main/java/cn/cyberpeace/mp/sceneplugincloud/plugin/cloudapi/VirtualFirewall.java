 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VirtualFirewall.java.
 // File size 4325.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualFirewallCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualFirewallDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualFirewallUpdateData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
class Match {
    private String sourceIpAddress;
    private String destinationIpAddress;
    private String sourcePort;
    private String destinationPort;
    private Integer ipVersion;
    private String protocol;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class RuleCreateData implements CloudHttpData {
    private String firewallId;
    private Match match;
    private String policyActionType;
    private Integer priority;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class RuleCreateResponse {
    private String policyId;
    private String firewallId;
}

public class VirtualFirewall extends CloudClient {

    public VirtualFirewallCreateData create(VirtualFirewallCreateData createData) {
        heavyConcurrentWait();

        VirtualFirewallCreateData fwCreateReq = new VirtualFirewallCreateData();
        fwCreateReq.setName(createData.getName());
        fwCreateReq.setNetworks(createData.getNetworks());
        fwCreateReq.setNetwork(createData.getNetwork());
        fwCreateReq.setRouteStatics(createData.getRouteStatics());

        CloudResponse<VirtualFirewallCreateData> result = cloudPost(CloudUrlConstant.VIRTUAL_FIREWALL_CREATE_URL, fwCreateReq,
                null, new TypeReference<CloudResponse<VirtualFirewallCreateData>>() {
                }, true);
        if (!result.isSuccess()) {
            return result.getData();
        }
        var createResult = result.getData();
        var firewallId = createResult.getId();

        for (var rule: createData.getRules()) {
            RuleCreateData ruleCreateData = new RuleCreateData();
            ruleCreateData.setFirewallId(firewallId);
            ruleCreateData.setPolicyActionType(rule.getAction());
            ruleCreateData.setPriority(rule.getPriority());
            ruleCreateData.setMatch(new Match(rule.getSourceIpAddress(), rule.getDestinationIpAddress(), rule.getSourcePort(),
                    rule.getDestinationPort(), rule.getIpVersion(), rule.getProtocol()));
            CloudResponse<RuleCreateResponse> ruleResult = cloudPost(CloudUrlConstant.VIRTUAL_FIREWALL_POLICY_CREATE_URL,
                    ruleCreateData, null, new TypeReference<CloudResponse<RuleCreateResponse>>() {
                    }, true);
            if (!ruleResult.isSuccess()) {
                VirtualFirewallDeleteData deleteData = new VirtualFirewallDeleteData();
                List<String> ids = new ArrayList<>();
                ids.add(firewallId);
                deleteData.setIds(ids);
                this.delete(deleteData);
                return result.getData();
            }
        }
        return createResult;
    }

    public boolean delete(VirtualFirewallDeleteData deleteData) {
        heavyConcurrentWait();

        CloudResponse<Object> result = cloudDelete(CloudUrlConstant.VIRTUAL_FIREWALL_DELETE_URL, deleteData, null,
                new TypeReference<CloudResponse<Object>>() {
                }, true);
        return true;
    }

    public VirtualFirewallCreateData update(VirtualFirewallUpdateData updateData) {
        heavyConcurrentWait();

        CloudResponse<VirtualFirewallCreateData> result = cloudPost(CloudUrlConstant.VIRTUAL_FIREWALL_UPDATE_URL, updateData,
                null, new TypeReference<CloudResponse<VirtualFirewallCreateData>>() {
                }, true);
        return result.getData();
    }
}
