package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.job.base.init.MpInitiate;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.repository.SceneMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName SceneUpdateStatusInitJob
 * @Description 清理mp服务启动之前正在创建中的场景, 仅修改状态
 * @Author zhanglin
 * @Date 2022/9/21
 * @Version 1.0.0
 */
@Slf4j
@Component
public class SceneUpdateStatusInitJob {
    @Resource
    private SceneMapper sceneMapper;
    @Resource
    private CsceneAppConfig csceneAppConfig;

    @MpInitiate(id = "scene_update_status_init_job", description = "将mp服务启动之前正在创建中的场景状态改为启动失败")
    public void execute() {
        //忽略服务启动之前一定时间内的启动的场景
        LocalDateTime startServiceTime = LocalDateTime.now()
                .minusSeconds(csceneAppConfig.getIgnoreSceneCreatingSeconds());
        HumanCondition humanCondition = new HumanCondition().filterOrStart()
                .orAndStart()
                .andEqual(SceneModel::getEvent, SceneModel.Event.CREATE)
                .andEqual(SceneModel::getEventStatus, EventStatus.IN_PROGRESS)
                .andLessThanOrEqual(SceneModel::getCreateTime, startServiceTime)
                .orAndEnd()
                .filterOrEnd();
        List<SceneModel> creatingScenes = sceneMapper.list(humanCondition);
        if (CollectionUtils.isNotEmpty(creatingScenes)) {
            LocalDateTime now = LocalDateTime.now();
            log.info("current local date time is {}", now);
            for (SceneModel creatingScene : creatingScenes) {
                try {
                    log.info("scene {} is created before mp service is started, and will be set error status", creatingScene.getId());
                    creatingScene.setEventStatus(EventStatus.ABNORMAL);
                    sceneMapper.update(creatingScene);
                } catch (Exception e) {
                    log.error("failed to correct error status scene , scene id is {}", creatingScene.getId(), e);
                }
            }
        }
    }
}