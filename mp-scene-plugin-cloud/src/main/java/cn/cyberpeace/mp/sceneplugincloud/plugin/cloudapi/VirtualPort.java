package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.*;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VirtualPort extends CloudClient {

    public VirtualPortCreateResult create(VirtualPortCreateData createData) {
        heavyConcurrentWait();

        CloudResponse<VirtualPortCreateResult> result = cloudPost(CloudUrlConstant.CREATE_PORTS, createData, null, new TypeReference<>() {
        }, true);
        return result.getData();
    }

    public VirtualPortUpdateResult update(VirtualPortUpdateData updateData) {
        heavyConcurrentWait();

        CloudResponse<VirtualPortUpdateResult> result = cloudPatch(CloudUrlConstant.CREATE_PORTS, updateData, null, new TypeReference<>() {
        }, true);
        return result.getData();
    }

    public void modifyInstanceIp(ModifyInstanceIpData data) {
        heavyConcurrentWait();

        cloudPost(CloudUrlConstant.MODIFY_INSTANCE_IP, data, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
    }

    public CreateQosRuleData createQosRules(CreateQosRuleData data) {
        heavyConcurrentWait();

        CloudResponse<CreateQosRuleData> result = cloudPost(CloudUrlConstant.CREATE_QOS_RULE, data, null,
                new TypeReference<CloudResponse<CreateQosRuleData>>() {
        }, true);
        return result.getData();
    }

    public void updateQosRules(UpdateQosRuleData data) {
        heavyConcurrentWait();

        cloudPost(CloudUrlConstant.UPDATE_QOS_RULE, data, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
    }


}
