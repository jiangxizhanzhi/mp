package cn.cyberpeace.mp.collection.service;

import cn.cyberpeace.mp.collection.model.log.DeviceLog;
import cn.cyberpeace.mp.collection.model.log.PageableLogs;
import cn.cyberpeace.mp.collection.request.DeviceLogQueryRequest;
import cn.cyberpeace.mp.collection.service.repo.CommonDeviceLogRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.*;

@Service
@Slf4j
public class DeviceLogService {
    @Resource(name = "restHighLevelClient")
    private RestHighLevelClient client;

    @Resource
    private CommonDeviceLogRepo commonDeviceLogRepo;

    @Resource
    private ElasticsearchOperations elasticsearchOperations;


    // 新增文档
    public DeviceLog saveDocument(DeviceLog deviceLog) {
        return commonDeviceLogRepo.save(deviceLog);
    }

    /**
     * 按业务类型和设备类型统计日志条数
     *
     * @param logType     业务类型，目前只有dmf
     * @param deviceTypes 设备类型
     */
    public Map<String, Long> statisticByLogType(String logType, String[] deviceTypes) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery("log_type", logType))
                        .must(QueryBuilders.termsQuery("device_type", deviceTypes)))
                .build();

        // 添加聚合操作，按dev_type分组并统计每组的日志条数
        searchQuery.addAggregation(AggregationBuilders.terms("by_device_type").field("device_type"));
        try {
            SearchHits<DeviceLog> searchHits = elasticsearchOperations.search(searchQuery, DeviceLog.class);
            Terms terms = searchHits.getAggregations().get("by_device_type");
            Map<String, Long> result = new HashMap<>();
            for (Terms.Bucket bucket : terms.getBuckets()) {
                long count = bucket.getDocCount();
                String devType = bucket.getKeyAsString();
                result.put(devType, count);
            }
            return result;
        } catch (Exception ex) {
            log.error("query log statistics error", ex);
            return new HashMap<>();
        }
    }

    public PageableLogs searchLog(DeviceLogQueryRequest req) {
        BoolQueryBuilder boolQuery = boolQuery();
        if (StringUtils.isNotEmpty(req.getIp())) {
            boolQuery.must(matchQuery("device_ip", req.getIp().trim()));
        }
        if (StringUtils.isNotEmpty(req.getType())) {
            boolQuery.must(matchQuery("device_type", req.getType().trim()));
        }
        if (StringUtils.isNotEmpty(req.getLogType())) {
            boolQuery.must(matchQuery("log_type", req.getLogType().trim()));
        }


        if (StringUtils.isNotEmpty(req.getTimeStart()) && StringUtils.isNotEmpty(req.getTimeEnd())) {
            boolQuery.must(rangeQuery("log_time").gte(req.getTimeStart()).lte(req.getTimeEnd()));
        }

        // 获取总条数
        long totalHits = 0L;
        // 计算数据页数
        long totalPage = 0L;

        if (boolQuery.hasClauses()) {
            // 首先查询总条数
            NativeSearchQuery totalQuery = new NativeSearchQueryBuilder()
                    .withQuery(boolQuery)
                    .build();
            try {
                SearchHits<DeviceLog> countHits = elasticsearchOperations.search(totalQuery, DeviceLog.class);
                totalHits = countHits.getTotalHits();
                totalPage = totalHits % req.getPageSize() == 0 ? totalHits / req.getPageSize() : totalHits / req.getPageSize() + 1;
            } catch (Exception ex) {
                log.error("es search error", ex);
            }

        }

        List<DeviceLog> logs = new ArrayList<>();
        if (totalHits > 0L && req.getPageIndex() <= totalPage) {
            // 然后再分页查询(固定使用时间戳升序排列，避免分页数据错乱)
            NativeSearchQuery pageQuery = new NativeSearchQueryBuilder()
                    .withQuery(boolQuery)
                    .withSort(SortBuilders.fieldSort("@timestamp").order(SortOrder.ASC))
                    .withPageable(PageRequest.of(req.getPageIndex() - 1, req.getPageSize()))
                    .build();
            SearchHits<DeviceLog> pageHits = elasticsearchOperations.search(pageQuery, DeviceLog.class);
            // 读取分页数据
            SearchPage<DeviceLog> pageData = SearchHitSupport.searchPageFor(pageHits, PageRequest.of(req.getPageIndex() - 1, req.getPageSize()));

            // 读取查询结果的数据
            logs = pageData.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
        }

        return PageableLogs.builder()
                .total(totalHits)
                .size((long) logs.size())
                .pageSize(Long.valueOf(req.getPageSize()))
                .currentPage(Long.valueOf(req.getPageIndex()))
                .totalPage(totalPage)
                .data(logs)
                .build();
    }
}
