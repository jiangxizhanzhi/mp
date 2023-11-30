package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.mptool.ZipUtil;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincollection.plugin.BatchSceneHandler;
import cn.cyberpeace.mp.situationaware.constant.SicLaunchConfig;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

@Slf4j
@Service
public class HostStateExportService implements ApplicationRunner {
    private static Queue<String> QUEUE = new ArrayBlockingQueue<>(300);

    @Resource(name = "wazuhRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    @Resource
    private CsceneCommonService csceneCommonService;

    @Resource
    private DataCollectionService dataCollectionService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        FileUtils.cleanDirectory(new File("data_export"));
    }

    @Scheduled(cron = "0 0/10 * * * ?")
    public void clear() {
        List<String> notDeleteFiles = new ArrayList<>();
        while (!QUEUE.isEmpty()) {
            String fileName = QUEUE.poll();
            File toDeleteFile = new File("data_export/" + fileName);
            try {
                if (toDeleteFile.exists()) {
                    FileUtils.delete(toDeleteFile);
                }
            } catch (Exception e) {
                notDeleteFiles.add(fileName);
                log.error(e.getMessage());
            }
        }
        QUEUE.addAll(notDeleteFiles);
    }

    public void addDeleteFile(String file) {
        QUEUE.add(file);
    }

    public File exportHost(String id) {
        String sceneId = csceneCommonService.translateSceneId(id);
        BatchSceneHandler sceneHandler = new BatchSceneHandler(List.of(sceneId), null);
        QueryBuilder queryBuilder = QueryBuilders.termQuery("data.sic_launch_id", SicLaunchConfig.LAUNCH_ID_HOST_STATE);
        return this.doExportAndZip(sceneHandler, queryBuilder);
    }

    public File exportEvent(String id) {
        String sceneId = csceneCommonService.translateSceneId(id);
        BatchSceneHandler sceneHandler = new BatchSceneHandler(List.of(sceneId), null);

        BoolQueryBuilder should = QueryBuilders.boolQuery();

        // 文件变更
        should.should(QueryBuilders.termQuery("data.sic_launch_id", SicLaunchConfig.LAUNCH_ID_FILE));
        should.should(QueryBuilders.termQuery("rule.groups", "syscheck_file"));
        // 注册表变更
        should.should(QueryBuilders.termQuery("data.sic_launch_id", SicLaunchConfig.LAUNCH_ID_REGISTRY));
        should.should(QueryBuilders.termQuery("rule.groups", "syscheck_registry"));


        return this.doExportAndZip(sceneHandler, should);
    }

    public File exportVulnerability(String id) {
        String sceneId = csceneCommonService.translateSceneId(id);
        BatchSceneHandler sceneHandler = new BatchSceneHandler(List.of(sceneId), null);

        BoolQueryBuilder should = QueryBuilders.boolQuery();

        should.should(QueryBuilders.existsQuery("data.vulnerability"));

        return this.doExportAndZip(sceneHandler, should);
    }

    private File doExportAndZip(BatchSceneHandler sceneHandler, QueryBuilder query) {
        Map<String, String> agentNameToFileName = this.getAgentNameToFileName(sceneHandler);
        if (agentNameToFileName.isEmpty()) {
            return null;
        }
        // 生成临时目录 存放每个节点导出的文件
        File tmpDir = this.createDir("data_export/" + RandomStringUtils.random(10, true, true));
        List<String> indices = this.getIndices(sceneHandler);
        for (var e : agentNameToFileName.entrySet()) {
            String agentName = e.getKey();
            String dataFileName = e.getValue();

            // 创建存放数据的文件
            File dataFile = new File(tmpDir.getAbsolutePath() + "/" + dataFileName);
            this.doExportElement(indices, agentName, dataFile, query);
        }

        // 压缩文件
        File file = new File("data_export/" + RandomStringUtils.random(10, true, true) + ".zip");
        ZipUtil.zipDirectory(tmpDir, file);
        try {
            FileUtils.deleteDirectory(tmpDir);
        } catch (IOException exception) {
            log.error(exception.getMessage());
        }
        return file;
    }

    private void doExportElement(List<String> indices, String agentName, File dataFile, QueryBuilder query) {
        BoolQueryBuilder finalQuery = QueryBuilders.boolQuery();
        finalQuery.must(QueryBuilders.termQuery("agent.name", agentName));
        finalQuery.must(query);

        // 第一次查询 开启滚动查询
        SearchRequest request = this.getScrollSearchRequest(finalQuery, indices);
        SearchResponse searchResponse = null;
        SearchHit[] hitsScroll = null;
        try {
            searchResponse = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        hitsScroll = searchResponse.getHits().getHits();
        String scrollId = searchResponse.getScrollId();
        if (hitsScroll != null && hitsScroll.length > 0) {
            this.writeData(hitsScroll, dataFile);
        }

        // 滚动写入其他数据
        if (searchResponse.getHits().getTotalHits().value > hitsScroll.length) {
            while (hitsScroll != null && hitsScroll.length > 0) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(new Scroll(TimeValue.timeValueMinutes(10L)));
                hitsScroll = null;
                try {
                    var scrollResponse = restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);
                    hitsScroll = scrollResponse.getHits().getHits();
                    if (hitsScroll.length > 0) {
                        this.writeData(hitsScroll, dataFile);
                    }
                } catch (IOException exception) {
                    log.error(exception.getMessage());
                }
            }
        }

        //清除滚动，否则影响下次查询
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        try {
            restHighLevelClient.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        } catch (IOException exception) {
            log.error(exception.getMessage());
        }
    }

    private Map<String, String> getAgentNameToFileName(BatchSceneHandler sceneHandler) {
        Map<String, Integer> existElementNames = new HashMap<>();
        Map<String, String> agentNameToFileName = new HashMap<>();
        for (var e : sceneHandler.getAllEid2ElementMap().entrySet()) {
            var agentName = e.getKey();
            var element = e.getValue();
            if (element.getType() == BaseSceneElementModel.Type.TERMINAL) {
                // 生成每个节点的文件名称
                String elementName = element.getName();
                String dataFileName = element.getName();
                if (existElementNames.containsKey(elementName)) {
                    Integer num = existElementNames.get(elementName);
                    dataFileName = dataFileName + "_" + num + ".txt";
                    existElementNames.put(elementName, num + 1);
                } else {
                    dataFileName = dataFileName + ".txt";
                    existElementNames.put(elementName, 1);
                }
                agentNameToFileName.put(agentName, dataFileName);
            }
        }
        return agentNameToFileName;
    }

    private List<String> getIndices(BatchSceneHandler sceneHandler) {
        var startTime = sceneHandler.getEarliestStartTime();
        var endTime = sceneHandler.getLatestSceneRunningTime();
        return dataCollectionService.indexListBuilder(startTime, endTime);
    }

    private File createDir(String dirName) {
        File dir = new File(dirName);
        try {
            FileUtils.forceMkdir(dir);
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return dir;
    }

    private void writeData(SearchHit[] hitsScroll, File dataFile) {
        List<String> result = new ArrayList<>();
        for (var h : hitsScroll) {
            result.add(JsonCommon.stringify(h.getSourceAsMap()));
        }
        try {
            FileUtils.writeStringToFile(dataFile, StringUtils.join(result, "\r\n"), Charset.forName("utf8"));
        } catch (IOException exception) {
            log.error(exception.getMessage());
        }
    }

    private SearchRequest getScrollSearchRequest(QueryBuilder query, List<String> indices) {
        var searchBuilder = new SearchSourceBuilder();
        searchBuilder.query(query);
        searchBuilder.trackTotalHits(true);
        searchBuilder.size(2000);
        SearchRequest request = new SearchRequest();
        request.source(searchBuilder);
        request.scroll(new Scroll(TimeValue.timeValueMinutes(10L)));
        request.indices(indices.toArray(new String[]{}));
        request.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        return request;
    }
}
