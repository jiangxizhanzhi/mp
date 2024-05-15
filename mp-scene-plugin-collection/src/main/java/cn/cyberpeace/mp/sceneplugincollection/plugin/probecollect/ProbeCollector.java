package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSingleton;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControl;
import cn.cyberpeace.mp.situationaware.service.DataCollectionFlowTableService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class ProbeCollector {

    private DataCollectionFlowTableService dataCollectionFlowTableService;

    private String getWazuhHost(Integer cloudType, String collectorId) {
        String wazuhHost = "";
        switch (cloudType) {
            case 1:
                wazuhHost = CollectionSingleton.collectionAppConfig.getWazuhServerHost();
                break;
            case 2:
                wazuhHost = CollectionSingleton.collectionAppConfig.getWazuhPublicServerHost();
                break;
            case 3:
                wazuhHost = CollectionSingleton.collectionAppConfig.getWazuhPrivateServerHost();
                break;
            case 4:
                wazuhHost = CollectionSingleton.collectionAppConfig.getWazuhMixedServerHost();
                break;
            default:
                wazuhHost = CollectionSingleton.collectionAppConfig.getWazuhServerHost();
                break;
        }
        return wazuhHost;
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

    public List<String> generateProbeInstallScript(Integer cloudType, String systemType, String agentName, String collectorId,
                                                   WazuhControl.ControlConfigParam controlParam, boolean isContainer) {
        return WazuhCommon.generateWazuhAgentInstallScript(cloudType, systemType, agentName, getWazuhHost(cloudType, collectorId), controlParam, isContainer);
    }

}
