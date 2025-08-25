package cn.cyberpeace.mp.sceneplugincollection.service;

import cn.cyberpeace.mp.scene.service.IFlowMirror;
import cn.cyberpeace.mp.situationaware.service.NetworkTrafficGroupService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class FlowMirrorService implements IFlowMirror {

    @Resource
    private NetworkTrafficGroupService networkTrafficGroupService;

    @Override
    public void flowMirror(NetworkTrafficMirrorRequest request) {
        var networkTrafficMirrorRequest = new cn.cyberpeace.mp.situationaware.model.NetworkTrafficMirrorRequest();
        networkTrafficMirrorRequest.setEntity(request.getEntity());
        networkTrafficMirrorRequest.setArkimeInstanceNames(request.getArkimeInstanceNames());
        networkTrafficMirrorRequest.setVmInstanceIds(request.getVmInstanceIds());
        networkTrafficMirrorRequest.setContainerInstanceIds(request.getContainerInstanceIds());
        networkTrafficMirrorRequest.setSceneId(request.getSceneId());
        networkTrafficGroupService.doBindingEntityNetworkTrafficMirror(networkTrafficMirrorRequest);
    }
}
