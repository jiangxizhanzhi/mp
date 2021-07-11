package cn.cyberpeace.mp.cscene.init.jobs;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.repository.SceneMapper;
import cn.cyberpeace.mp.sceneplugincloud.init.jobs.AbstractInitJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;


@Slf4j
@Component
public class SceneUpdateStatusJob extends AbstractInitJob {
    @Resource
    private SceneMapper sceneMapper;
    @Resource
    private CsceneAppConfig csceneAppConfig;

    @Override
    protected boolean doExecute() {
        
        var startServiceTime = LocalDateTime.now().minusSeconds(csceneAppConfig.getIgnoreSceneCreatingSeconds());
        HumanCondition humanCondition = new HumanCondition()
                .filterOrStart()
                .orAndStart()
                .andEqual(SceneModel::getEvent, SceneModel.Event.CREATE)
                .andEqual(SceneModel::getEventStatus, EventStatus.IN_PROGRESS)
                .andLessThanOrEqual(SceneModel::getCreateTime, startServiceTime)
                .orAndEnd()
                .filterOrEnd();
        var creatingScenes = sceneMapper.list(humanCondition);
        if (CollectionUtils.isNotEmpty(creatingScenes)) {
            LocalDateTime now = LocalDateTime.now();
            log.info("current local date time is {}", now);
            for (var creatingScene : creatingScenes) {
                try {
                    log.info("scene {} is created before mp service is started, and will be set error status", creatingScene.getId());
                    creatingScene.setEventStatus(EventStatus.ABNORMAL);
                    sceneMapper.update(creatingScene);
                } catch (Exception e) {
                    log.error("failed to correct error status scene , scene id is {}", creatingScene.getId(), e);
                }
            }
        }
        return true;
    }

    @Override
    protected boolean getSupportRefresh() {
        return false;
    }
}