package cn.cyberpeace.mp.collection.service;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.collection.core.es.IRestHighLevelClient;
import cn.cyberpeace.mp.collection.model.contest.ContestLogModel;
import cn.cyberpeace.mp.collection.model.contest.RuntimeLogModel;
import cn.cyberpeace.mp.collection.request.IndexQueryRequest;
import cn.cyberpeace.mp.collection.request.LogQueryRequest;
import cn.cyberpeace.mp.collection.util.EsResponseParseUtil;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class ContestLogService {
    @Resource(name = "restHighLevelClientImpl")
    private IRestHighLevelClient restHighLevelClient;

    @Resource
    private BaseAppConfig baseAppConfig;

    public static final String INDEX_MP_CONTEST_LOG = "mp_contest_log";

    public void addContestLog(ContestLogModel contestLogModel) {
        Map<String, Object> source = JSON.parseObject(JSON.toJSONString(contestLogModel), Map.class);
        IndexRequest indexRequest = new IndexRequest(INDEX_MP_CONTEST_LOG).source(source);
        restHighLevelClient.index(indexRequest);
    }

    public void addRuntimeLog(RuntimeLogModel runtimeLogModel) {
        String time = TimeUtils.localDateToString(runtimeLogModel.getTime().toLocalDate());
        String index = runtimeLogModel.getType() + "_" + runtimeLogModel.getIp() + "_" + runtimeLogModel.getLogType() + "_" + time + ".txt";
//        Map<String, Object> source = JSON.parseObject(JSON.toJSONString(JSON.parse(runtimeLogModel.getMessage())), Map.class);
//        IndexRequest indexRequest = new IndexRequest(index).source(source);
//        restHighLevelClient.index(indexRequest);
        String logDir = StringUtils.joinWith(FileUtil.FILE_SEPARATOR, baseAppConfig.getHomeDir(), "contest");
        File dir = new File(logDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(StringUtils.joinWith(FileUtil.FILE_SEPARATOR, logDir, index));
        FileWriter fileWriter = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
                fileWriter = new FileWriter(file.getPath(), true);
                fileWriter.write(JSON.toJSONString(JSON.parse(runtimeLogModel.getMessage())));
            } else {
                fileWriter = new FileWriter(file.getPath(), true);
                fileWriter.write("\n" + JSON.toJSONString(JSON.parse(runtimeLogModel.getMessage())));
            }
        } catch (Exception e) {
            log.error("file write error:{}", ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(BaseErrorCode.ERROR);
        } finally {
            try {
                assert fileWriter != null;
                fileWriter.close();
            } catch (Exception e) {

            }
        }
    }

    public ListObjResult<ContestLogModel> queryContestLog(LogQueryRequest request) {
        var sourceBuilder = buildSearchSourceBuilder(request);
        var boolQueryBuilder = buildBoolQueryBuilder(request);
        SearchRequest searchRequest = buildSearchRequest(sourceBuilder, boolQueryBuilder);

        SearchResponse response = restHighLevelClient.search(searchRequest);
        List<ContestLogModel> contestLogModels = new ArrayList<>();
        if (Objects.isNull(response) || Objects.isNull(response.getHits()) || Objects.isNull(response.getHits().getHits())) {
            return new ListObjResult<>(contestLogModels, 0);
        }
        contestLogModels = EsResponseParseUtil.parseResponse(response, ContestLogModel.class);
        long total = 0L;
        if (Objects.nonNull(response.getHits()) && Objects.nonNull(response.getHits().getTotalHits())) {
            total = response.getHits().getTotalHits().value;
        }
        return new ListObjResult<>(contestLogModels, BigDecimal.valueOf(total).intValue());
    }

    public ListObjResult<Map> queryByIndex(IndexQueryRequest request) {
        if (StringUtils.isEmpty(request.getIndex())) {
            return new ListObjResult<>(new ArrayList<>(), 0);
        }
        var sourceBuilder = new SearchSourceBuilder();
//        sourceBuilder.sort("time", SortOrder.DESC);
        int offset = (request.getPageNum() - 1) * (request.getPageSize());
        sourceBuilder.from(Math.min(offset, 10000));
        if ((offset + request.getPageSize()) > 10000) {
            sourceBuilder.size(Math.max(10000 - offset, 0));
        } else {
            sourceBuilder.size(request.getPageSize());
        }

        var searchRequest = new SearchRequest(request.getIndex());
        searchRequest.source(sourceBuilder);
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        var response = restHighLevelClient.search(searchRequest);
        List<Map> objects = new ArrayList<>();
        if (Objects.isNull(response) || Objects.isNull(response.getHits()) || Objects.isNull(response.getHits().getHits())) {
            return new ListObjResult<>(objects, 0);
        }
        objects = EsResponseParseUtil.parseResponse(response, Map.class);
        long total = 0L;
        if (Objects.nonNull(response.getHits()) && Objects.nonNull(response.getHits().getTotalHits())) {
            total = response.getHits().getTotalHits().value;
        }
        return new ListObjResult<>(objects, BigDecimal.valueOf(total).intValue());
    }

    private SearchSourceBuilder buildSearchSourceBuilder(LogQueryRequest request) {
        var sourceBuilder = new SearchSourceBuilder();
        // 按时间排序
        sourceBuilder.sort("time", SortOrder.DESC);
        //处理分页
        processPageable(request, sourceBuilder);
        return sourceBuilder;
    }

    private void processPageable(LogQueryRequest request, SearchSourceBuilder sourceBuilder) {
        int offset = (request.getPageNum() - 1) * (request.getPageSize());
        sourceBuilder.from(Math.min(offset, 10000));
        if ((offset + request.getPageSize()) > 10000) {
            sourceBuilder.size(Math.max(10000 - offset, 0));
        } else {
            sourceBuilder.size(request.getPageSize());
        }
    }

    private BoolQueryBuilder buildBoolQueryBuilder(LogQueryRequest request) {
        var boolQueryBuilder = QueryBuilders.boolQuery();
        //构造查询条件
        buildLogBoolQueryBuilder(request, boolQueryBuilder);
        return boolQueryBuilder;
    }

    private void buildLogBoolQueryBuilder(LogQueryRequest request, BoolQueryBuilder boolQueryBuilder) {
        if (CollectionUtils.isNotEmpty(request.getIps())) {
            buildIpQuery(request.getIps(), boolQueryBuilder);
        }

        if (CollectionUtils.isNotEmpty(request.getAttackIps())) {
            buildAttackIpQuery(request.getAttackIps(), boolQueryBuilder);
        }

        if (CollectionUtils.isNotEmpty(request.getSeverity())) {
            buildSeverityQuery(request.getSeverity(), boolQueryBuilder);
        }

        if (Objects.nonNull(request.getStartTime())) {
            //开始时间查询
            boolQueryBuilder.must(QueryBuilders.rangeQuery("time")
                    .from(request.getStartTime()));
        }

        if (Objects.nonNull(request.getEndTime())) {
            //结束时间查询
            boolQueryBuilder.must(QueryBuilders.rangeQuery("time")
                    .to(request.getEndTime()));
        }
    }

    private void buildIpQuery(List<String> ips, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.termsQuery("ip", ips));
    }

    private void buildAttackIpQuery(List<String> attackIps, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.termsQuery("attackIp", attackIps));
    }

    private void buildSeverityQuery(List<Integer> severity, BoolQueryBuilder boolQueryBuilder) {
        boolQueryBuilder.filter(QueryBuilders.termsQuery("severity", severity));
    }

    private SearchRequest buildSearchRequest(SearchSourceBuilder sourceBuilder, BoolQueryBuilder boolQueryBuilder) {
        var searchRequest = new SearchRequest(INDEX_MP_CONTEST_LOG);

        searchRequest.source(sourceBuilder.query(boolQueryBuilder));

        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        return searchRequest;
    }
}
