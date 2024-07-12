 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VirtualPort.java.
 // File size 2474.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.*;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName VirtualPort
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudClient
 */
@Slf4j
public class VirtualPort extends CloudClient {

    /**
     * create
     *
     * @param createData
     * @return
     */
    public VirtualPortCreateResult create(VirtualPortCreateData createData) {
        heavyConcurrentWait();

        CloudResponse<VirtualPortCreateResult> result = cloudPost(CloudUrlConstant.CREATE_PORTS, createData, null, new TypeReference<>() {
        }, true);
        return result.getData();
    }

    /**
     * update
     *
     * @param updateData
     * @return
     */
    public VirtualPortUpdateResult update(VirtualPortUpdateData updateData) {
        heavyConcurrentWait();

        CloudResponse<VirtualPortUpdateResult> result = cloudPatch(CloudUrlConstant.CREATE_PORTS, updateData, null, new TypeReference<>() {
        }, true);
        return result.getData();
    }

    /**
     * modifyInstanceIp
     *
     * @param data
     */
    public void modifyInstanceIp(ModifyInstanceIpData data) {
        heavyConcurrentWait();

        cloudPost(CloudUrlConstant.MODIFY_INSTANCE_IP, data, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
    }

    /**
     * createQosRules
     *
     * @param data
     * @return
     */
    public CreateQosRuleData createQosRules(CreateQosRuleData data) {
        heavyConcurrentWait();

        CloudResponse<CreateQosRuleData> result = cloudPost(CloudUrlConstant.CREATE_QOS_RULE, data, null,
                new TypeReference<CloudResponse<CreateQosRuleData>>() {
        }, true);
        return result.getData();
    }

    /**
     * updateQosRules
     *
     * @param data
     */
    public void updateQosRules(UpdateQosRuleData data) {
        heavyConcurrentWait();

        cloudPost(CloudUrlConstant.UPDATE_QOS_RULE, data, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
    }


}
