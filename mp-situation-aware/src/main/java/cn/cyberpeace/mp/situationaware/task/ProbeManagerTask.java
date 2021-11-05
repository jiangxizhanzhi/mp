package cn.cyberpeace.mp.situationaware.task;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.base.util.es.EsConstants;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareErrorCode;
import cn.cyberpeace.mp.situationaware.model.ProbeModel;
import cn.cyberpeace.mp.situationaware.model.enums.ProbeStatus;
import cn.cyberpeace.mp.situationaware.repository.ProbeModelMapper;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 *
 **/
@Component
@Slf4j
public class ProbeManagerTask {

    @Autowired
    private ProbeModelMapper probeModelMapper;

    @Resource(name = "wazuhRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private SituationAwareConfig situationAwareConfig;

    @Scheduled(fixedRate = 1000 * 60)
    private void processProbeStatus() {
        if(!situationAwareConfig.getTaskEnable()){
            log.info("ProbeManagerTask disabled");
            return;
        }
        List<ProbeModel> probeModelList = probeModelMapper.queryAllProbes();
        probeModelList.forEach(model -> {
            ProbeStatus probeStatus = Boolean.TRUE.equals(queryProbeExist(model)) ? ProbeStatus.RUNNING : ProbeStatus.TERMINATE;
            //状态不同的需要修改状态
            if (!probeStatus.equals(model.getProbeStatus())) {
                probeModelMapper.updateProbeStatus(model.getId(), probeStatus);
            }
        });
    }

    @VisibleForTesting
    protected Boolean queryProbeExist(ProbeModel probeModel) {
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(Long.parseLong(probeModel.getCommandInterval().replace("m", "") + 2L));
        var endTime = LocalDateTime.now();
        //构建请求索引
        String[] indexArray = DataCollectionService.buildRequestIndex(startTime, endTime);
        var searchRequest = new SearchRequest(indexArray);
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();
        boolQueryBuilder.must(QueryBuilders.termQuery("rule.id", "86701"));
        boolQueryBuilder.must(QueryBuilders.termQuery("location", "command_" + probeModel.getProbeName()));
        //时间查询
        boolQueryBuilder.must(QueryBuilders.rangeQuery("timestamp")
            .from(TimeUtils.localDateTimeToString(startTime, EsConstants.esDateTimeFormatter))
            .to(TimeUtils.localDateTimeToString(endTime, EsConstants.esDateTimeFormatter)));
        sourceBuilder.sort("timestamp", SortOrder.DESC);
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));

        SearchResponse response = null;
        try {
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("探针es查询失败 探针id: {},失败异常: {}", probeModel.getId(), ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(SituationalAwareErrorCode.PROBE_MANAGER_ES_QUERY_ERROR, ExceptionUtils.getStackTrace(e));
        }
        return Objects.requireNonNull(response).getHits().getHits().length > 0;
    }

}
