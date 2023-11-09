package cn.cyberpeace.mp.cscene.init;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.repository.SceneMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @Description
 * @Author maojun
 * @Date 2022/8/5
 */
@Slf4j
@Component
public class SceneDeletedErrorClean {

    @Resource
    private SceneMapper sceneMapper;

    @Scheduled(cron = "0 0/3 * * * ?")
    public void execute() {
        // 5分钟没删除结束的 再尝试一次
        var list = sceneMapper.list(new HumanCondition()
                .filterEqual(SceneModel::getEvent, SceneModel.Event.DELETE)
                .filterEqual(SceneModel::getEventStatus, EventStatus.IN_PROGRESS)
                .filterLessThan(SceneModel::getUpdateTime, LocalDateTime.now().plusMinutes(-5)));
        if (list != null && list.size() > 0) {
            for (var s : list) {
                try {
                    new SceneHandler(s.getId(), null, null).logicDelete();
                } catch (Exception e) {
                    log.error("failed to delete scene {}", s.getId(), e);
                }
            }
        }
    }
}
