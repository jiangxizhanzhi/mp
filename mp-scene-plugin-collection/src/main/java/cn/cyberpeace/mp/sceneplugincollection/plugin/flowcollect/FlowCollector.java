package cn.cyberpeace.mp.sceneplugincollection.plugin.flowcollect;

import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import cn.cyberpeace.mp.situationaware.model.NetworkTrafficMirrorRequest;
import cn.cyberpeace.mp.situationaware.service.NetworkTrafficGroupService;
import cn.cyberpeace.mp.situationaware.service.NetworkTrafficService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Data
@Component
public class FlowCollector {

    private SituationAwareConfig situationAwareConfig;

    private NetworkTrafficGroupService networkTrafficGroupService;

    private NetworkTrafficService networkTrafficService;

    @Autowired
    public FlowCollector(NetworkTrafficGroupService networkTrafficGroupService,
                         NetworkTrafficService networkTrafficService,SituationAwareConfig situationAwareConfig) {
        setNetworkTrafficGroupService(networkTrafficGroupService);
        setNetworkTrafficService(networkTrafficService);
        setSituationAwareConfig(situationAwareConfig);
    }

    public String createCollector() {
        var collectorName = networkTrafficGroupService.addArkimeImageInstance(null);

        //查询groupId对应的实例镜像
        String result = null;
        try {
            result = HttpUtils.sendGet(situationAwareConfig.getArkimeCpcloudContainerCreateUrl() + "/containers?name=" + collectorName);
        } catch (IOException e) {
            log.error("query cloud containers error, {}", ExceptionUtils.getStackTrace(e));
        }
        JSONArray networkList = (JSONArray) JSONPath.eval(JSON.parse(result), "$.data.list");
        AtomicReference<String> containerId = new AtomicReference<>("");
        networkList.stream().filter(json -> json instanceof JSONObject).map(JSONObject.class::cast).filter(json -> collectorName.equals(json.get("name"))).findFirst().ifPresent(container ->{
            containerId.set((String) container.get("id"));
        });

        Container container = new Container();
        container.addRoute(containerId.get());

        return collectorName;
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
        // 删除实例
        networkTrafficGroupService.deleteArkimeInstance(collectorId);
        // 删除镜像
        var request = new NetworkTrafficMirrorRequest();
        request.setArkimeInstanceNames(List.of(collectorId));
        request.setVmInstanceIds(vmInstanceIds);
        request.setContainerInstanceIds(containerInstanceIds);
        networkTrafficGroupService.deleteNetworkTrafficMirror(request);
    }

    public void deleteEntityMirror(List<String> vmInstanceIds, List<String> containerInstanceIds) {
        // 删除镜像
        var request = new NetworkTrafficMirrorRequest();
        request.setVmInstanceIds(vmInstanceIds);
        request.setContainerInstanceIds(containerInstanceIds);
        networkTrafficGroupService.deleteNetworkTrafficMirror(request);
    }

}
