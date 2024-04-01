package cn.cyberpeace.mp.job.cron;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.job.schedule.MpScheduled;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.context.CsceneContextHolder;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.cscene.service.action.CsceneCallAction;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.repository.SceneMapper;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConnectionModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConnectionMapper;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: guquan
 * @Date：2023/6/19 14:07
 */
@Slf4j
@Component
public class UserElementConnectionInfoJob {

    @Autowired
    private SceneMapper sceneMapper;

    @Autowired
    private CloudPluginSceneConnectionMapper CloudPluginSceneConnectionMapper;

    @Autowired
    private ScenePluginCloudSceneService scenePluginCloudSceneService;

    @Autowired
    private CsceneInstanceService csceneInstanceService;

    @Autowired
    private CsceneInstanceMapper csceneInstanceMapper;

    @MpScheduled(cron = "*/10 * * * * ?", description = "扫描当前虚拟机的接入状态,用于推送给前端接入信息的变化", id = "scan_user_element_connection")
    public void execute() {
        log.debug("ConnectionJob.execute begin.");
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual(SceneModel::getDeleted, false);
        List<SceneModel> sceneModelList = sceneMapper.list(humanCondition);
        if (CollectionUtils.isEmpty(sceneModelList)) {
            log.debug("No scene should send connection info.");
            return;
        }
        List<String> idList = new ArrayList<>();
        for (SceneModel sceneModel : sceneModelList) {
            String id = sceneModel.getId();
            idList.add(id);
        }
        HumanCondition humanConditionConnection = new HumanCondition();
        humanConditionConnection.filterIn(CloudPluginSceneConnectionModel::getScene, idList.toArray());
        humanConditionConnection.filterEqual(CloudPluginSceneConnectionModel::getDisable, false);
        //防止有些脏数据，只针对8小时以内的接入数据处理
        humanConditionConnection.filterGreaterThan(CloudPluginSceneConnectionModel::getUpdateTime, LocalDateTime.now().minusHours(8));
        //对于刚接入的设备，云平台需要时间才能成为接入状态。防止定时任务误把还没接入的设备当成断开的数据处理
        humanConditionConnection.filterLessThan(CloudPluginSceneConnectionModel::getUpdateTime, LocalDateTime.now().minusSeconds(5));
        List<CloudPluginSceneConnectionModel> cloudPluginSceneConnectionModelList = CloudPluginSceneConnectionMapper.list(humanConditionConnection);
        if (CollectionUtils.isEmpty(cloudPluginSceneConnectionModelList)) {
            log.debug("CloudPluginSceneConnectionModelList is empty. No connection should been send.");
            return;
        }
        List<String> connectionIdList = new ArrayList<>();
        for (CloudPluginSceneConnectionModel cloudPluginSceneConnectionModel : cloudPluginSceneConnectionModelList) {
            String connectionId = cloudPluginSceneConnectionModel.getConnectionId();
            connectionIdList.add(connectionId);
        }
        List<String> activeConnectionIdList = new Remote().activeConnection(connectionIdList);
        if (activeConnectionIdList.size() == connectionIdList.size()) {
            log.debug("List size is same. No connection should been send.");
            return;
        }
        List<CloudPluginSceneConnectionModel> disConnectionList = new ArrayList<>();
        for (CloudPluginSceneConnectionModel temp : cloudPluginSceneConnectionModelList) {
            if (connectionIdList.contains(temp.getConnectionId()) && !activeConnectionIdList.contains(temp.getConnectionId())) {
                disConnectionList.add(temp);
            }
        }
        if (CollectionUtils.isEmpty(disConnectionList)) {
            log.debug("No connection should been send.");
            return;
        }
        for (CloudPluginSceneConnectionModel connectionModel : disConnectionList) {
            HumanCondition csceneInstanceHumanCondition = new HumanCondition();
            csceneInstanceHumanCondition.filterEqual(CsceneInstanceModel::getDeleted, false);
            csceneInstanceHumanCondition.filterEqual(CsceneInstanceModel::getScene, connectionModel.getScene());
            CsceneInstanceModel csceneInstanceModel = csceneInstanceMapper.first(csceneInstanceHumanCondition);
            if (csceneInstanceModel == null) {
                log.info("cscene not exist.");
                continue;
            }
            try {
                CsceneContextHolder.setCallAction(CsceneCallAction.NODE_ACCESS_USER);
                CsceneContextHolder.setCsceneInstanceId(csceneInstanceModel.getId());
                CsceneContextBO context = new CsceneContextBO();
                context.setPassThrough(StringUtils.isEmpty(csceneInstanceModel.getPassThrough()) ? new HashMap<>() : JsonCommon.parse(csceneInstanceModel.getPassThrough(), new TypeReference<Map<String, Object>>() {
                }));
                if (context.getPassThrough() == null) {
                    CsceneInstanceBO csceneInstance = new CsceneInstanceBO();
                    csceneInstance.fromModel(csceneInstanceModel);
                }
                context.loadTransparentContext();
                scenePluginCloudSceneService.disableRemoteRecording(connectionModel.getScene(), connectionModel.getSceneElement(), connectionModel.getAccessUser(), connectionModel.getConnectionId());
                csceneInstanceService.reportElementAccessUser(connectionModel.getScene(), connectionModel.getSceneElement());
            } finally {
                CsceneContextHolder.resetContext();
            }
        }
        log.debug("ConnectionJob.execute end.");
    }
}
