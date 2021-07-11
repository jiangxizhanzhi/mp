package cn.cyberpeace.mp.cscene;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.core.event.base.ProcessStatus;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.scene.repository.SceneMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;


@Service
@Slf4j
public class SceneCorrectService {

    @Resource
    private SceneMapper sceneMapper;

    @Resource
    private CsceneAppConfig csceneAppConfig;

    @Resource
    private SceneElementMapper sceneElementMapper;

    public void correctErrorStatusScene() {
        correctStartErrorStatusScene();
        correctDeleteErrorStatusScene();
    }

    private void correctStartErrorStatusScene() {
        HumanCondition humanCondition = new HumanCondition()
                .filterOrStart()
                .orAndStart()
                .andEqual(SceneModel::getEvent, SceneModel.Event.INITIATE)
                .andNotEqual(SceneModel::getEventStatus, EventStatus.ABNORMAL)
                .orAndEnd()
                .orAndStart()
                .andEqual(SceneModel::getEvent, SceneModel.Event.CREATE)
                .andIn(SceneModel::getEventStatus, new Integer[]{
                        EventStatus.START, EventStatus.IN_PROGRESS
                })
                .orAndEnd()
                .filterOrEnd();
        var creatingScenes = sceneMapper.list(humanCondition);
        if (CollectionUtils.isNotEmpty(creatingScenes)) {
            LocalDateTime now = LocalDateTime.now();
            log.info("current local date time is {}", now);
            for (var creatingScene : creatingScenes) {
                try {
                    var createTime = creatingScene.getCreateTime();
                    if (createTime.plusSeconds(csceneAppConfig.getSceneErrorCreatingSeconds()).isBefore(LocalDateTime.now())) {
                        
                        int counts = sceneElementMapper.total(new HumanCondition()
                                .filterEqual(SceneElementModel::getScene, creatingScene.getId())
                        );
                        long finalTimes = csceneAppConfig.getSceneErrorCreatingSeconds() + counts * csceneAppConfig.getElementErrorCreatingSeconds();
                        log.info("check scene is created before {} seconds ago", finalTimes);
                        if (createTime.plusSeconds(finalTimes).isBefore(now)) {
                            log.info("scene {} is created before {} seconds ago , and will be set error status", creatingScene.getId(), finalTimes);
                            creatingScene.setEventStatus(EventStatus.ABNORMAL);
                            creatingScene.setProcessStatus(ProcessStatus.ABNORMAL);
                            creatingScene.setProcessMessage(MessageCommon.get("cscene.error.scene_create_timeout"));
                            sceneMapper.update(creatingScene);
                        }
                    }
                } catch (Exception e) {
                    log.error("failed to correct error status scene , scene id is {}", creatingScene.getId(), e);
                }
            }
        }
    }

    private void correctDeleteErrorStatusScene() {
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(SceneModel::getEvent, SceneModel.Event.DELETE)
                .filterIn(SceneModel::getEventStatus, new Integer[]{
                        EventStatus.INITIAL, EventStatus.START, EventStatus.IN_PROGRESS, EventStatus.PAUSE
                });
        var creatingScenes = sceneMapper.list(humanCondition);
        if (CollectionUtils.isNotEmpty(creatingScenes)) {
            LocalDateTime now = LocalDateTime.now();
            log.info("current local date time is {}", now);
            for (var creatingScene : creatingScenes) {
                try {
                    var createTime = creatingScene.getUpdateTime();
                    if (createTime.plusSeconds(csceneAppConfig.getSceneErrorCreatingSeconds()).isBefore(LocalDateTime.now())) {
                        
                        int counts = sceneElementMapper.total(new HumanCondition()
                                .filterEqual(SceneElementModel::getScene, creatingScene.getId())
                        );
                        long finalTimes = csceneAppConfig.getSceneErrorCreatingSeconds() + counts * csceneAppConfig.getElementErrorCreatingSeconds();
                        log.info("check scene is delete before {} seconds ago", finalTimes);
                        if (createTime.plusSeconds(finalTimes).isBefore(now)) {
                            log.info("scene {} is delete before {} seconds ago , and will be set over status", creatingScene.getId(), finalTimes);
                            creatingScene.setEventStatus(EventStatus.OVER);
                            creatingScene.setProcessStatus(ProcessStatus.OVER);
                            sceneMapper.update(creatingScene);
                            SceneElementModel updateElement = new SceneElementModel();
                            updateElement.setEventStatus(EventStatus.OVER);
                            updateElement.setProcessStatus(ProcessStatus.OVER);
                            sceneElementMapper.filterUpdate(updateElement, new HumanCondition()
                                    .filterEqual(SceneElementModel::getScene, creatingScene.getId())
                            );
                        }
                    }
                } catch (Exception e) {
                    log.error("failed to correct error status scene , scene id is {}", creatingScene.getId(), e);
                }
            }
        }
    }
}
