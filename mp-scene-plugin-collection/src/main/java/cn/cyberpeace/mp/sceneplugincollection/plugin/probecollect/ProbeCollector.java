 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ProbeCollector.java.
 // File size 2731.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSingleton;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControl;
import cn.cyberpeace.mp.situationaware.service.DataCollectionFlowTableService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName ProbeCollector
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@Component
public class ProbeCollector {

    private DataCollectionFlowTableService dataCollectionFlowTableService;

    /**
     * getWazuhHost
     *
     * @param collectorId
     * @return
     */
    private String getWazuhHost(String collectorId) {
        return CollectionSingleton.collectionAppConfig.getWazuhServerHost();
    }

    /**
     * ProbeCollector
     *
     * @param dataCollectionFlowTableService
     */
    @Autowired
    public ProbeCollector(DataCollectionFlowTableService dataCollectionFlowTableService) {
        this.dataCollectionFlowTableService = dataCollectionFlowTableService;
    }

    /**
     * 目前方案固定一个wazuh server
     */
    public String createCollector() {
        return TextUtils.uuid();
    }

    /**
     * bindAgent
     *
     * @param collectorId
     * @param agentInstanceId
     */
    public void bindAgent(String collectorId, String agentInstanceId) {
        dataCollectionFlowTableService.createDataCollectionFlow(List.of(agentInstanceId));
    }

    /**
     * unbindAgent
     *
     * @param collectorId
     * @param agentInstanceId
     */
    public void unbindAgent(String collectorId, String agentInstanceId) {
        dataCollectionFlowTableService.deleteDataCollectionFlow(List.of(agentInstanceId));
    }

    /**
     * deleteCollector
     *
     * @param collectorId
     */
    public void deleteCollector(String collectorId) {
    }

    /**
     * generateProbeInstallScript
     *
     * @param systemType
     * @param agentName
     * @param collectorId
     * @param controlParam
     * @param isContainer
     * @return
     */
    public List<String> generateProbeInstallScript(String systemType, String agentName, String collectorId,
                                                   WazuhControl.ControlConfigParam controlParam, boolean isContainer) {
        return WazuhCommon.generateWazuhAgentInstallScript(systemType, agentName, getWazuhHost(collectorId), controlParam, isContainer);
    }

}
