package cn.cyberpeace.mp.cscene.job;

import cn.cyberpeace.mp.base.cron.MpScheduled;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class CleanUselessEsDataCronJob {

    @Resource
    private CsceneInstanceMapper csceneInstanceMapper;

    @Resource
    private ScenePluginCollectionSceneService collectionSceneService;

    @MpScheduled(cron = "0 0 1 * * ?", id = "clean_useless_es_data", description = "清理已关闭场景中无效的es数据")
    public void action() {
        LocalDateTime twoDaysAgo = LocalDateTime
                .now()
                .minusDays(2);

        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(CsceneInstanceModel::getDeleted, true)
                .filterGreaterThan(CsceneInstanceModel::getUpdateTime, twoDaysAgo);
        List<CsceneInstanceModel> csceneInstances = csceneInstanceMapper.list(humanCondition);
        if (CollectionUtils.isEmpty(csceneInstances)) {
            return;
        }

        for (CsceneInstanceModel csceneInstance : csceneInstances) {
            String sceneId = csceneInstance.getScene();
            try {
                collectionSceneService.cleanUselessESData(sceneId);
            } catch (Exception e) {
                log.error("failed to clean one scene , scene id is " + sceneId, e);
            }
        }
    }

}
