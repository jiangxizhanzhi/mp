package cn.cyberpeace.mp.situationaware.task;

import cn.cyberpeace.mp.situationaware.model.ProbeModel;
import cn.cyberpeace.mp.situationaware.repository.ProbeModelMapper;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 **/
@Component
public class ProbeManagerTask {

    @Autowired
    private ProbeModelMapper probeModelMapper;

    @Resource(name = "wazuhRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    private static final String WAZUH_ALERT_INDEX_PREFIX = "wazuh-alerts-4.x-";

    @Scheduled(fixedRate = 1000 * 60)
    private void processProbeStatus() {
        List<ProbeModel> probeModelList = probeModelMapper.queryAllProbes();
        probeModelList.forEach(model -> {

        });
    }

    @VisibleForTesting
    protected Map<String, Boolean> queryProbeExist(ProbeModel probeModel) {
        Map<String, Boolean> result = Maps.newHashMap();
        //构建请求索引
        String[] indexArray = DataCollectionService.buildRequestIndex(LocalDateTime.now().minusMinutes(Long.parseLong(probeModel.getCommandInterval().replace("m", ""))), LocalDateTime.now());
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();
        boolQueryBuilder.must(QueryBuilders.termQuery("rule.id", "86701"));
        boolQueryBuilder.must(QueryBuilders.termQuery("location", "command_" + probeModel.getProbeName()));
        sourceBuilder.sort("timestamp", SortOrder.DESC);
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));
        SearchResponse response = null;
        try {
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        result.put(probeModel.getId(), response.getHits().getHits().length > 0);
        return result;
    }
}
