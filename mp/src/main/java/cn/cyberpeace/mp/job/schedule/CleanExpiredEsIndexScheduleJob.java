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
import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeRequest;
import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private static final String  PREFIX = "wazuh-alerts-4.x-";

    private final List<String> CLEAR_INDEX_NAMES = new ArrayList<>();


    @MpScheduled(cron = "0 10 1 * * ?", id = "clean_expired_es_data", description = "每个活动场景保留15天的ES数据,天数可配")
    public void action() {
        log.info("clean expired es data start");
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
                HumanCondition queryCollectionScene = new HumanCondition().filterEqual(CollectionPluginSceneModel::getScene, sceneId);
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

        cleanWazuhMonitorData();
        clearWazuhSize();
        CLEAR_INDEX_NAMES.clear();
        log.info("clean expired es data end");
    }

    /**
     * 清理非当天的wazuh监控数据
     */
    private void cleanWazuhMonitorData(){
        // 清理非当天的数据, 项目中监控数据只使用了一条；
        Set<String> excludeIndexs = new HashSet<>();

        LocalDateTime nDaysAgo = LocalDateTime.now();
        String excludeDeleteDataIndex = TimeUtils.localDateTimeToString(nDaysAgo, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        excludeIndexs.add(PREFIX + excludeDeleteDataIndex);

        LocalDateTime oneDaysAgo = LocalDateTime.now().minusDays(1);
        excludeDeleteDataIndex = TimeUtils.localDateTimeToString(oneDaysAgo, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        excludeIndexs.add(PREFIX + excludeDeleteDataIndex);

        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
        excludeDeleteDataIndex = TimeUtils.localDateTimeToString(twoDaysAgo, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        excludeIndexs.add(PREFIX + excludeDeleteDataIndex);

        GetIndexRequest getIndexRequest = new GetIndexRequest(PREFIX + "*");
        try {

            GetIndexResponse result = restHighLevelClient.indices().get(getIndexRequest, RequestOptions.DEFAULT);
            if (result.getIndices() != null && result.getIndices().length > 0) {
                final String[] indices = result.getIndices();
                for (String indexName : indices) {
                    boolean isExclude = false;
                    for (String excludeIndex : excludeIndexs) {
                        if (indexName.contains(excludeIndex)) {
                            isExclude = true;
                        }
                    }
                    if (isExclude) {
                        continue;
                    }
                    FuzzyQueryBuilder controllerQuery = QueryBuilders.fuzzyQuery("agent.name", "controller");
                    FuzzyQueryBuilder computerQuery = QueryBuilders.fuzzyQuery("agent.name", "computer");
                    BoolQueryBuilder combinedQuery = QueryBuilders.boolQuery()
                            .should(controllerQuery)
                            .should(computerQuery);

                    DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(indexName);
                    deleteByQueryRequest.setQuery(combinedQuery);

                    BulkByScrollResponse response = restHighLevelClient.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
                    log.info("clean wazuh data, indexName is {}, delete {} records", indexName, response.getDeleted());
                    if (response.getDeleted() > 0) {
                        CLEAR_INDEX_NAMES.add(indexName);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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
                String nDaysAgoStr = TimeUtils.localDateTimeToString(nDaysAgo, DateTimeFormatter.ofPattern("yyMMdd"));
                int nDaysAgoYYYYMMDD = Integer.valueOf(nDaysAgoStr);
                for (String index : indexs) {
                    String dayStr = StringUtils.replace(index, sessionPrefix, "");
                    if (NumberUtils.isCreatable(dayStr)) {
                        int YYYYMMDD = Integer.parseInt(dayStr);
                        if (YYYYMMDD < nDaysAgoYYYYMMDD) {
                            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest();
                            deleteIndexRequest.indices(index);
                            final AcknowledgedResponse delete = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
                            log.info("delete index {} result is {}", index, delete.isAcknowledged());
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

        String toDeleteDataIndex = TimeUtils.localDateTimeToString(nDaysAgo.minusDays(1), DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        String index = PREFIX + toDeleteDataIndex;

        BatchSceneHandler sceneHandler = new BatchSceneHandler(Arrays.asList(scene.getScene()), new ArrayList<>());
        List<String> agentNames = new ArrayList<>(sceneHandler.getAllEid2ElementMap().keySet());

        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(index);
        deleteByQueryRequest.setQuery(QueryBuilders.termsQuery("agent.name", agentNames));

        try {
            final BulkByScrollResponse bulkByScrollResponse = restHighLevelClient.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
            log.info("clean wazuh data, scene id is {}, delete {} records", scene.getScene(), bulkByScrollResponse.getDeleted());
            if (bulkByScrollResponse.getDeleted() > 0) {
                CLEAR_INDEX_NAMES.add(index);
            }

        } catch (Exception e) {
            log.error("failed to clean data , scene id is " + scene.getScene(), e);
        }
    }

    /**
     * 释放删除数据后的空间， 比较消耗es的性能
     */
    private void clearWazuhSize() {
        for (String indexName : CLEAR_INDEX_NAMES) {
            // 删除数据后空间不会被立马回收，需要执行强制回收命令
            // Step 1: 创建 ForcemergeRequest 对象
            ForceMergeRequest request = new ForceMergeRequest(indexName);
            // Step 2: 设置 max_num_segments 为 1
            request.maxNumSegments(1);
            request.onlyExpungeDeletes(true);
            ForceMergeResponse forceMergeResponse;
            try {
                forceMergeResponse = restHighLevelClient.indices().forcemerge(request, RequestOptions.DEFAULT);
            } catch (IOException e) {
                log.error("failed to clear wazuh data, index name is {}", indexName, e);
                return;
            }
            log.info("force merge result is {}", forceMergeResponse.getStatus());
        }
    }

}
