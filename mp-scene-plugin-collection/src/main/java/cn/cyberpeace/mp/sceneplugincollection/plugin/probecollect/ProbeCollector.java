package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSingleton;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Data
@Component
public class ProbeCollector {

    private String getWazuhHost(String collectorId) {
        return CollectionSingleton.collectionAppConfig.getWazuhServerHost();
    }

    @Autowired
    public ProbeCollector() {
    }

    public String createCollector() {
        return TextUtils.uuid();
    }

    public void deleteCollector(String collectorId) {
    }

    public String generateProbeInstallScript(String systemType, String agentName, String collectorId) {
        return WazuhCommon.generateWazuhAgentInstallScript(systemType, agentName, getWazuhHost(collectorId));
    }

}
