package cn.cyberpeace.mp.job.schedule;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.job.base.schedule.MpScheduled;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.repository.SceneMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class SceneDeleteScheduleJob {

    @Resource
    private SceneMapper sceneMapper;

    @MpScheduled(cron = "0 0/1 * * * ?", description = "长时间处于错误状态的场景及时删除", id = "delete_error_scene")
    public void scheduleUserOlRecord() {
        var cond = new HumanCondition()
                .link(CsceneInstanceModel.class, SceneModel.class)
                .filterLink(CsceneInstanceModel.class, CsceneInstanceModel::getScene, SceneModel.class,SceneModel::getId)
                .currentLink(CsceneInstanceModel.class)
                .filterEqual(CsceneInstanceModel::getDeleted,true)
                .currentLink(SceneModel.class)
                .filterNotEqual(SceneModel::getEvent,SceneModel.Event.DELETE);

        var list = sceneMapper.list(cond);
        if (CollectionUtils.isNotEmpty(list)) {
            for (var scene : list) {
                new SceneHandler(scene.getId(), null, null).logicDelete();
            }
        }
    }
}
