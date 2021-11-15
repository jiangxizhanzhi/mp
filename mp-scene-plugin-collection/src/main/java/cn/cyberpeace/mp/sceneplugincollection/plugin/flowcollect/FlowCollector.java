package cn.cyberpeace.mp.sceneplugincollection.plugin.flowcollect;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.situationaware.model.GroupModel;
import cn.cyberpeace.mp.situationaware.service.NetworkTrafficGroupService;
import cn.cyberpeace.mp.situationaware.service.NetworkTrafficService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class FlowCollector {

    private NetworkTrafficGroupService networkTrafficGroupService;

    private NetworkTrafficService networkTrafficService;

    @Autowired
    public FlowCollector(NetworkTrafficGroupService networkTrafficGroupService,
                         NetworkTrafficService networkTrafficService) {
        setNetworkTrafficGroupService(networkTrafficGroupService);
        setNetworkTrafficService(networkTrafficService);
    }

    public String createCollector(List<String> macAddrs) {
        var groupData = new GroupModel();
        String collectorId = TextUtils.uuid();
        groupData.setGroupId(collectorId);
        groupData.setMacIds(macAddrs);
        networkTrafficGroupService.addArkimeImageInstance(groupData);
        return collectorId;
    }

    public void deleteCollector(String collectorId) {
        networkTrafficGroupService.deleteArkimeInstance(collectorId);
    }

}
