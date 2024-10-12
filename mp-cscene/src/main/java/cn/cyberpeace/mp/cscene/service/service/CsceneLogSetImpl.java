package cn.cyberpeace.mp.cscene.service.service;

import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.cscene.repository.mapper.CsceneLogSetMapper;
import cn.cyberpeace.mp.cscene.request.logset.LogSetRequest;
import cn.cyberpeace.mp.cscene.vo.SceneTypeVO;
import cn.cyberpeace.mp.sceneplugincollection.plugin.BatchSceneHandler;
import cn.cyberpeace.mp.situationaware.service.ProbeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class CsceneLogSetImpl {

    // 场景采集数据默认保存周期7天
    private static final Integer DEFAULT_CYCLE = 7;

    @Autowired
    private CsceneLogSetMapper csceneLogSetMapper;

    @Autowired
    private ProbeService probeService;

    public Boolean modifySceneCollectionDataCycle(LogSetRequest requestData) {
        Boolean result = csceneLogSetMapper.modifySceneCollectionDataCycle(requestData);
        return result;
    }

    // 定时任务 每天一点执行
//    @Scheduled(cron = "0 0 1 * * ?")
    @Scheduled(cron = "0 55 11 * * ?")
    public void cleanCollectedDataTask() {
        log.info("cleanCollectedDataTask start!");
        // 查询所有存活的场景
        List<SceneTypeVO> list = csceneLogSetMapper.selectLivedSceneInstance();
        if (list != null && list.size() > 0) {
            // 删除超出时限的es数据
            list.forEach(scene -> cleanCollectedData(scene));
        }
    }

    public void cleanCollectedData(SceneTypeVO scene) {
        Integer days = csceneLogSetMapper.getSceneCollectionDataCycle(scene.getSceneInstanceId());
        if (days == null) {
            // 设置默认存储周期
            days = 7;
        }
        Integer finalDays = days;
        TaskCommon.executeTask(() -> {
            log.info("cleanCollectedDataTask start! sceneId:{}", scene.getSceneId());
            BatchSceneHandler batchSceneHandler = new BatchSceneHandler(Arrays.asList(scene.getSceneId()), null);
            // 删除采集的数据
            List<String> probeEnabledEid = new ArrayList<>(batchSceneHandler.getProbeEnabledNodeId2EidMap().values());
            if (CollectionUtils.isNotEmpty(probeEnabledEid)) {
                probeService.cleanProbeDataByCycle(probeEnabledEid, batchSceneHandler.getEarliestStartTime(), finalDays, scene.getCloudType());
            }
            log.info("cleanCollectedDataTask end! sceneId:{}", scene.getSceneId());
        });
    }

    public Integer getSceneCollectionDataCycle(String sceneId) {
        // 无数据则为默认值7
        return csceneLogSetMapper.getSceneCollectionDataCycle(sceneId) != null ?
                csceneLogSetMapper.getSceneCollectionDataCycle(sceneId) : DEFAULT_CYCLE;
    }
}

