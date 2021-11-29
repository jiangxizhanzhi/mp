package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSingleton;
import cn.cyberpeace.mp.situationaware.service.DataCollectionFlowTableService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class ProbeCollector {

    private DataCollectionFlowTableService dataCollectionFlowTableService;

    private String getWazuhHost(String collectorId) {
        return CollectionSingleton.collectionAppConfig.getWazuhServerHost();
    }

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

    public void bindAgent(String collectorId, String agentInstanceId) {
        dataCollectionFlowTableService.createDataCollectionFlow(List.of(agentInstanceId));
    }

    public void unbindAgent(String collectorId, String agentInstanceId) {
        dataCollectionFlowTableService.deleteDataCollectionFlow(List.of(agentInstanceId));
    }

    public void deleteCollector(String collectorId) {
    }

    public String generateProbeInstallScript(String systemType, String agentName, String collectorId) {
        return WazuhCommon.generateWazuhAgentInstallScript(systemType, agentName, getWazuhHost(collectorId));
    }

}
