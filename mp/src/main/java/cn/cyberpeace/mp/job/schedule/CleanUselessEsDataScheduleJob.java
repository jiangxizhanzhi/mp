 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CleanUselessEsDataScheduleJob.java.
 // File size 3014.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.job.schedule;

import cn.cyberpeace.mp.job.base.schedule.MpScheduled;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneModel;
import cn.cyberpeace.mp.sceneplugincollection.repository.CollectionPluginSceneMapper;
import cn.cyberpeace.mp.situationaware.service.FlowAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName CleanUselessEsDataScheduleJob
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Component
public class CleanUselessEsDataScheduleJob {

    @Resource
    private CsceneInstanceMapper csceneInstanceMapper;

    @Resource
    private CollectionPluginSceneMapper collectionPluginSceneMapper;

    @Resource
    private FlowAnalysisService flowAnalysisService;

    @MpScheduled(cron = "0 0 1 * * ?", id = "clean_useless_es_data", description = "清理最近关闭的场景中多余的es数据")
    public void action() {
        LocalDateTime twoDaysAgo = LocalDateTime.now()
                .minusDays(2);
        int page = 1;
        int pageSize = 50;
        int maxBatchSize = 50;
        HumanCondition humanCondition = new HumanCondition().filterEqual(CsceneInstanceModel::getDeleted, true)
                .filterGreaterThan(CsceneInstanceModel::getUpdateTime, twoDaysAgo)
                .orderBy(CsceneInstanceModel::getPk)
                .pagination(page, pageSize);
        List<CsceneInstanceModel> csceneInstances = csceneInstanceMapper.list(humanCondition);
        while (CollectionUtils.isNotEmpty(csceneInstances) && page < maxBatchSize) {
            page++;
            for (CsceneInstanceModel csceneInstance : csceneInstances) {
                String sceneId = csceneInstance.getScene();
                var queryCollectionScene = new HumanCondition().filterEqual(CollectionPluginSceneModel::getScene, sceneId);
                CollectionPluginSceneModel scene = collectionPluginSceneMapper.first(queryCollectionScene);
                if (scene == null || StringUtils.isEmpty(scene.getFlowCollector())) {
                    continue;
                }
                try {
                    flowAnalysisService.clearUselessArkimeESIndex(scene.getFlowCollector());
                } catch (Exception e) {
                    log.error("failed to clean one scene , scene id is " + sceneId, e);
                }
            }
            humanCondition.pagination(page, pageSize);
            csceneInstances = csceneInstanceMapper.list(humanCondition);
        }
    }

}
