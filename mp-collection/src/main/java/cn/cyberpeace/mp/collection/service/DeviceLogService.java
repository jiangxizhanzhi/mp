package cn.cyberpeace.mp.collection.service;

import cn.cyberpeace.mp.collection.model.log.DeviceLog;
import cn.cyberpeace.mp.collection.model.log.PageableLogs;
import cn.cyberpeace.mp.collection.request.DeviceLogQueryRequest;
import cn.cyberpeace.mp.collection.service.repo.CommonDeviceLogRepo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

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
    public Iterable<DeviceLog>  saveDocuments(Iterable<DeviceLog> deviceLogList) {
        return commonDeviceLogRepo.saveAll(deviceLogList);
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
                .withPageable(Pageable.ofSize(1))
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
        Query query = req.convertToQuery();
        Page<DeviceLog> deviceLogPage = commonDeviceLogRepo.pageDeviceLogByQuery(query);

        return PageableLogs.builder()
                .total(deviceLogPage.getTotalElements())
                .size((long) deviceLogPage.getContent().size())
                .pageSize(Long.valueOf(req.getPageSize()))
                .currentPage(Long.valueOf(req.getPageIndex()))
                .totalPage((long) deviceLogPage.getTotalPages())
                .data(deviceLogPage.getContent())
                .build();
    }
}
