package cn.cyberpeace.mp.sceneplugincollection.plugin.flowcollect;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.sceneplugincollection.util.resourcename.ICollectionResourceName;
import cn.cyberpeace.mp.situationaware.model.NetworkTrafficMirrorRequest;
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

    public String createCollector() {
        return networkTrafficGroupService.addArkimeImageInstance(null);
    }

    public void bindEntityNetworkPorts(String sceneId, List<String> vmInstanceIds, List<String> containerInstanceIds) {
        var request = new NetworkTrafficMirrorRequest();
        request.setVmInstanceIds(vmInstanceIds);
        request.setContainerInstanceIds(containerInstanceIds);
        request.setSceneId(sceneId);
        networkTrafficGroupService.doBindingEntityNetworkTrafficMirror(request);
    }

    public void bindNetworkPorts(String sceneId, String collectorId, List<String> vmInstanceIds, List<String> containerInstanceIds) {
        var request = new NetworkTrafficMirrorRequest();
        request.setArkimeInstanceNames(List.of(collectorId));
        request.setVmInstanceIds(vmInstanceIds);
        request.setContainerInstanceIds(containerInstanceIds);
        request.setSceneId(sceneId);
        networkTrafficGroupService.doBindingNetworkTrafficMirror(request);
    }

    public void bindNetworkPorts(String sceneId, String collectorId, List<String> vmInstanceIds, List<String> containerInstanceIds,boolean entity) {
        var request = new NetworkTrafficMirrorRequest();
        request.setArkimeInstanceNames(List.of(collectorId));
        request.setVmInstanceIds(vmInstanceIds);
        request.setContainerInstanceIds(containerInstanceIds);
        request.setSceneId(sceneId);
        request.setEntity(entity);
        networkTrafficGroupService.doBindingNetworkTrafficMirror(request);
    }

    public void unbindNetworkPorts(String collectorId, List<String> macAddrs) {
    }

    public void deleteCollector(String collectorId, List<String> vmInstanceIds, List<String> containerInstanceIds) {
        
        networkTrafficGroupService.deleteArkimeInstance(collectorId);
        
        var request = new NetworkTrafficMirrorRequest();
        request.setArkimeInstanceNames(List.of(collectorId));
        request.setVmInstanceIds(vmInstanceIds);
        request.setContainerInstanceIds(containerInstanceIds);
        networkTrafficGroupService.deleteNetworkTrafficMirror(request);
    }

    public void deleteEntityMirror(List<String> vmInstanceIds, List<String> containerInstanceIds) {
        
        var request = new NetworkTrafficMirrorRequest();
        request.setVmInstanceIds(vmInstanceIds);
        request.setContainerInstanceIds(containerInstanceIds);
        networkTrafficGroupService.deleteNetworkTrafficMirror(request);
    }

}
