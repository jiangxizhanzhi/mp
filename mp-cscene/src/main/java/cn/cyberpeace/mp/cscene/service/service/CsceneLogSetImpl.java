package cn.cyberpeace.mp.cscene.service.service;

import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.cscene.repository.mapper.CsceneLogSetMapper;
import cn.cyberpeace.mp.cscene.request.logset.LogSetRequest;
import cn.cyberpeace.mp.sceneplugincollection.plugin.BatchSceneHandler;
import cn.cyberpeace.mp.situationaware.service.ProbeService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CsceneLogSetImpl {

    @Autowired
    private CsceneLogSetMapper csceneLogSetMapper;

    @Autowired
    private ProbeService probeService;

    public Boolean modifySceneCollectionDataCycle(LogSetRequest requestData) {
        Boolean result = csceneLogSetMapper.modifySceneCollectionDataCycle(requestData);
        return result;
    }

    // 定时任务 每天凌晨一点执行
    @Scheduled(cron = "0 0 1 * * ?")
    public void cleanCollectesDataTask() {
        // 查询所有存活的场景
        List<String> sceneIds = csceneLogSetMapper.selectLivedSceneInstance();
        if (sceneIds != null && sceneIds.size() > 0) {
            // 删除超出时限的es数据
            sceneIds.forEach(sceneId -> cleanCollectesData(sceneId));
        }
    }

    public void cleanCollectesData(String sceneId) {
        Integer days = csceneLogSetMapper.getSceneCollectionDataCycle(sceneId);
        if (days != null || days == 0) {
            // 设置默认存储周期
            days = 7;
        }
        Integer finalDays = days;
        TaskCommon.executeTask(() -> {
            BatchSceneHandler batchSceneHandler = new BatchSceneHandler(Arrays.asList(sceneId), null);
            // 删除采集的数据
            List<String> probeEnabledEid = new ArrayList<>(batchSceneHandler.getProbeEnabledNodeId2EidMap().values());
            if (CollectionUtils.isNotEmpty(probeEnabledEid)) {
                probeService.cleanProbeDataByCycle(probeEnabledEid, batchSceneHandler.getEarliestStartTime(), finalDays);
            }
        });
    }

    public Integer getSceneCollectionDataCycle(String sceneId) {
        // 无数据则为默认值7
        return csceneLogSetMapper.getSceneCollectionDataCycle(sceneId) != null ? csceneLogSetMapper.getSceneCollectionDataCycle(sceneId) : 7;
    }
}

