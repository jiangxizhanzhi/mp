package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.SystemTypeUtils;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostEventBO;
import cn.cyberpeace.mp.sceneplugincollection.plugin.BatchSceneHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.service.collect.base.WazuhResult;
import cn.cyberpeace.mp.sceneplugincollection.service.collect.filemodify.WazuhFileModifyService;
import cn.cyberpeace.mp.situationaware.model.FileEventModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class CsceneHostCollectQueryService {

    @Resource
    private CsceneInstanceService csceneInstanceService;

    @Resource
    private WazuhFileModifyService wazuhFileModifyService;

    public ListObjServiceResult<CollectionHostEventBO> getFileModifyData(int from, int size, String csceneId, List<String> nodeIds, LocalDateTime queryStartAt, LocalDateTime queryEndAt, List<Integer> levels, String filePath) {
        String sceneId = csceneInstanceService.getSceneId(csceneId);
        if (StringUtils.isEmpty(sceneId)) {
            return new ListObjServiceResult<>(new ArrayList<>(), 0);
        }

        BatchSceneHandler sceneHandler = new BatchSceneHandler(Arrays.asList(sceneId), nodeIds);
        CollectionScenePluginHandler collectionScenePluginHandler = sceneHandler.getCollectionScenePluginHandler(sceneId);
        CloudScenePluginHandler cloudScenePluginHandler = sceneHandler.getCloudScenePluginHandler(sceneId);
        // 无采集插件 直接返回
        if (collectionScenePluginHandler == null) {
            return new ListObjServiceResult<>(new ArrayList<>(), 0);
        }

        LocalDateTime startTime = queryStartAt != null ? queryStartAt : sceneHandler.getEarliestStartTime();
        LocalDateTime endTime = queryEndAt != null ? queryEndAt : sceneHandler.getLatestSceneRunningTime();
        Map<String, SceneElementBO> agentNameToElement = sceneHandler.getAllEid2ElementMap();

        List<String> agentNames = new ArrayList<>(agentNameToElement.keySet());
        WazuhResult<FileEventModel> result = wazuhFileModifyService.queryFileModifyData(from, size, agentNames, startTime, endTime, levels, filePath);

        List<CollectionHostEventBO> hostEvents = new ArrayList<>();
        for (FileEventModel fileEventModel : result.getList()) {
            CollectionHostEventBO hostEvent = new CollectionHostEventBO();
            hostEvent.setSceneId(csceneId);
            this.writeBaseInfo(fileEventModel, hostEvent);
            this.writeNodeInfo(fileEventModel.getAgentName(), sceneHandler, cloudScenePluginHandler, hostEvent);
            hostEvent.setAppName("");
            hostEvents.add(hostEvent);
        }
        return new ListObjServiceResult<>(hostEvents, (int) result.getTotal());
    }

    private void writeNodeInfo(String nodeEid, BatchSceneHandler sceneHandler, CloudScenePluginHandler cloudScenePluginHandler, CollectionHostEventBO hostEvent) {
        SceneElementBO srcElement = sceneHandler.getNodeByEid(nodeEid);
        if (srcElement != null) {
            hostEvent.setNodeId(srcElement.getId());
            hostEvent.setNodeName(srcElement.getName());
            hostEvent.setSystemType(srcElement.getSystemType());
            var item = SystemTypeUtils.getSystemTypeItem(srcElement.getSystemType());
            hostEvent.setSystemTypeName(item.getName());
            if (cloudScenePluginHandler != null) {
                var cloudElement = cloudScenePluginHandler.getElement(srcElement.getId());
                if (cloudElement != null) {
                    hostEvent.setVirtualType(cloudElement.getVirtualType());
                }
            }
        }
    }

    private void writeBaseInfo(FileEventModel fileEventModel, CollectionHostEventBO hostEventBO) {
        hostEventBO.setType(fileEventModel.getFileEventType().name());
        if (StringUtils.isEmpty(fileEventModel.getAction())) {
            hostEventBO.setEventDesc(fileEventModel.getMessage());
        } else {
            hostEventBO.setEventDesc(fileEventModel.getAction());
        }
        hostEventBO.setTime(fileEventModel.getUpdateTime());
        hostEventBO.setDescription(fileEventModel.getDescription());
        hostEventBO.setLevel(fileEventModel.getLevel());
        hostEventBO.setCatagory(fileEventModel.getCatagory());
        hostEventBO.setContent(fileEventModel.getContent());
        hostEventBO.setPath(fileEventModel.getFilePath());
        hostEventBO.setFileSize(fileEventModel.getSize());
        hostEventBO.setMd5Before(fileEventModel.getMd5Before());
        hostEventBO.setMd5After(fileEventModel.getMd5After());
        hostEventBO.setScrollId(fileEventModel.getScrollId());
    }

}
