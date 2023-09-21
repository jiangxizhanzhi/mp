package cn.cyberpeace.mp.collection.service;

import cn.cyberpeace.mp.collection.model.log.CommonDeviceLog;
import cn.cyberpeace.mp.collection.model.log.DeviceLog;
import cn.cyberpeace.mp.collection.service.repo.CommonDeviceLogRepo;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class DeviceLogService {
    @Resource(name = "restHighLevelClient")
    private RestHighLevelClient client;

    @Resource
    private CommonDeviceLogRepo commonDeviceLogRepo;


    // 新增文档
    public CommonDeviceLog saveDocument(CommonDeviceLog deviceLog) {
        return commonDeviceLogRepo.save(deviceLog);
    }

    // 查询文档
    public List<DeviceLog> searchDocuments(String indexName, String field, String value) throws IOException {
        SearchRequest request = new SearchRequest(indexName);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery(field, value));
        sourceBuilder.timeout(TimeValue.timeValueSeconds(5));
        request.source(sourceBuilder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        // TODO: 处理查询结果并返回设备日志列表
        // List<DeviceLog> deviceLogs = processSearchResponse(response);

        return null;
    }
}
