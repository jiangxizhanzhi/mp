package cn.cyberpeace.mp.cscene.service.cron;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.repository.SceneMapper;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConnectionModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConnectionMapper;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: guquan
 * @Date：2023/6/19 14:07
 */
@Slf4j
@Component
public class ConnectionJob {

    @Autowired
    private SceneMapper sceneMapper;

    @Autowired
    private CloudPluginSceneConnectionMapper CloudPluginSceneConnectionMapper;

    @Autowired
    private ScenePluginCloudSceneService scenePluginCloudSceneService;

    @Autowired
    private CsceneInstanceService csceneInstanceService;

    @Scheduled(cron = "*/5 * * * * ?")
    public void execute() {
        log.info("ConnectionJob.execute begin.");
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual(SceneModel::getDeleted, false);
        List<SceneModel> sceneModelList = sceneMapper.list(humanCondition);
        if (CollectionUtils.isEmpty(sceneModelList)) {
            log.info("No scene should send connection info.");
            return;
        }
        List<String> idList = sceneModelList.stream().map(SceneModel::getId).collect(Collectors.toList());
        HumanCondition humanConditionConnection = new HumanCondition();
        humanConditionConnection.filterIn(CloudPluginSceneConnectionModel::getScene, idList.toArray());
        humanConditionConnection.filterEqual(CloudPluginSceneConnectionModel::getDisable, false);
        List<CloudPluginSceneConnectionModel> cloudPluginSceneConnectionModelList = CloudPluginSceneConnectionMapper.list(humanConditionConnection);
        if (CollectionUtils.isEmpty(cloudPluginSceneConnectionModelList)) {
            log.info("CloudPluginSceneConnectionModelList is empty. No connection should been send.");
            return;
        }
        List<String> connectionIdList = cloudPluginSceneConnectionModelList.stream().map(CloudPluginSceneConnectionModel::getConnectionId).collect(Collectors.toList());
        List<String> activeConnectionIdList = new Remote().activeConnection(connectionIdList);
        if (activeConnectionIdList.size() == connectionIdList.size()) {
            log.info("List size is same. No connection should been send.");
            return;
        }
        List<CloudPluginSceneConnectionModel> disConnectionList = new ArrayList<>();
        for (CloudPluginSceneConnectionModel temp : cloudPluginSceneConnectionModelList) {
            if (connectionIdList.contains(temp.getConnectionId()) && !activeConnectionIdList.contains(temp.getConnectionId())) {
                disConnectionList.add(temp);
            }
        }
        if (CollectionUtils.isEmpty(disConnectionList)) {
            log.info("No connection should been send.");
            return;
        }
        for (CloudPluginSceneConnectionModel connectionModel : disConnectionList) {
            scenePluginCloudSceneService.disableRemoteRecording(connectionModel.getScene(), connectionModel.getSceneElement(), connectionModel.getAccessUser(), connectionModel.getConnectionId());
            csceneInstanceService.reportElementAccessUser(connectionModel.getScene(), connectionModel.getSceneElement());
        }
        log.info("ConnectionJob.execute end.");
    }
}
