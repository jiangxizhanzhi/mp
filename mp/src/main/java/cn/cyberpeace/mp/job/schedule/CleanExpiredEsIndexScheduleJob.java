package cn.cyberpeace.mp.job.schedule;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.frontadmin.model.SysConfigModel;
import cn.cyberpeace.mp.frontadmin.repository.SysConfigMapper;
import cn.cyberpeace.mp.job.base.schedule.MpScheduled;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneModel;
import cn.cyberpeace.mp.sceneplugincollection.plugin.BatchSceneHandler;
import cn.cyberpeace.mp.sceneplugincollection.repository.CollectionPluginSceneElementMapper;
import cn.cyberpeace.mp.sceneplugincollection.repository.CollectionPluginSceneMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class CleanExpiredEsIndexScheduleJob {

    @Resource(name = "wazuhRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    @Resource
    private SysConfigMapper sysConfigMapper;

    @Resource
    private CsceneInstanceMapper csceneInstanceMapper;

    @Resource
    private CollectionPluginSceneMapper collectionPluginSceneMapper;

    @Resource
    private CollectionPluginSceneElementMapper elementMapper;

    @MpScheduled(cron = "0 * * * * ?", id = "clean_expired_es_data", description = "每个活动场景保留15天的ES数据,天数可配")
    public void action() {
        int nDays = 15;
        String key = "clean_expired_es_data_day";
        SysConfigModel sysConfig = sysConfigMapper.first(new HumanCondition().filterEqual(SysConfigModel::getKeyName, key));
        if (sysConfig != null) {
            nDays = Integer.parseInt(sysConfig.getValue());
        }
        LocalDateTime nDaysAgo = LocalDateTime.now().minusDays(nDays);
        int page = 1;
        int pageSize = 50;
        int maxBatchSize = 50;
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(CsceneInstanceModel::getDeleted, false)
                .orderBy(CsceneInstanceModel::getPk)
                .pagination(page, pageSize);
        List<CsceneInstanceModel> csceneInstances = csceneInstanceMapper.list(humanCondition);
        while (CollectionUtils.isNotEmpty(csceneInstances) && page < maxBatchSize) {
            page++;
            for (CsceneInstanceModel csceneInstance : csceneInstances) {
                String sceneId = csceneInstance.getScene();
                var queryCollectionScene = new HumanCondition().filterEqual(CollectionPluginSceneModel::getScene, sceneId);
                CollectionPluginSceneModel scene = collectionPluginSceneMapper.first(queryCollectionScene);
                if (scene == null) {
                    continue;
                }
                this.cleanArkimeSessionData(scene, nDaysAgo);
                this.cleanWazuhSessionData(scene, nDaysAgo);
            }
            humanCondition.pagination(page, pageSize);
            csceneInstances = csceneInstanceMapper.list(humanCondition);
        }
    }

    private void cleanArkimeSessionData(CollectionPluginSceneModel scene, LocalDateTime nDaysAgo) {
        if (StringUtils.isEmpty(scene.getFlowCollector())) {
            return;
        }
        String sessionPrefix = scene.getFlowCollector() + "_sessions3-";
        GetIndexRequest getIndexRequest = new GetIndexRequest(sessionPrefix + "*");
        try {
            GetIndexResponse result = restHighLevelClient.indices().get(getIndexRequest, RequestOptions.DEFAULT);
            String[] indexs = result.getIndices();
            if (indexs != null) {
                String nDaysAgoStr = TimeUtils.localDateTimeToString(nDaysAgo, DateTimeFormatter.ofPattern("yyyyMMdd"));
                int nDaysAgoYYYYMMDD = Integer.valueOf(nDaysAgoStr);
                for (String index : indexs) {
                    String dayStr = StringUtils.replace(index, sessionPrefix, "");
                    if (NumberUtils.isCreatable(dayStr)) {
                        int YYYYMMDD = Integer.parseInt(dayStr);
                        if (YYYYMMDD < nDaysAgoYYYYMMDD) {
                            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest();
                            deleteIndexRequest.indices(index);
                            restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("failed to clean index , prefix is " + scene.getFlowCollector(), e);
        }
    }

    private void cleanWazuhSessionData(CollectionPluginSceneModel scene, LocalDateTime nDaysAgo) {
        HumanCondition elementCondition = new HumanCondition().filterEqual(CollectionPluginSceneElementModel::getScene, scene.getScene())
                .filterEqual(CollectionPluginSceneElementModel::getEnableProbeCollect, true);
        List<CollectionPluginSceneElementModel> elementModels = elementMapper.list(elementCondition);
        if (CollectionUtils.isEmpty(elementModels)) {
            return;
        }

        String prefix = "wazuh-alerts-4.x-";
        String toDeleteDataIndex = TimeUtils.localDateTimeToString(nDaysAgo.minusDays(1), DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        String index = prefix + toDeleteDataIndex;

        BatchSceneHandler sceneHandler = new BatchSceneHandler(Arrays.asList(scene.getScene()), new ArrayList<>());
        List<String> agentNames = new ArrayList<>(sceneHandler.getAllEid2ElementMap().keySet());

        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(index);
        deleteByQueryRequest.setQuery(QueryBuilders.termsQuery("agent.name", agentNames));

        try {
            restHighLevelClient.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("failed to clean data , scene id is " + scene.getScene(), e);
        }
    }

}
