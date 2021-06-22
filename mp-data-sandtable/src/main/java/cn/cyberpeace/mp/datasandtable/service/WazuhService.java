package cn.cyberpeace.mp.datasandtable.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.util.LocalDateTimeUtils;
import cn.cyberpeace.mp.datasandtable.dao.WazuhDao;
import cn.cyberpeace.mp.datasandtable.model.FileEventModel;
import cn.cyberpeace.mp.datasandtable.model.WazuhFileEventRequest;
import cn.cyberpeace.mp.datasandtable.model.WazuhFileRoot;
import cn.cyberpeace.mp.datasandtable.model.emum.FileEventType;
import com.alibaba.fastjson.JSONObject;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * wazuh对接
 */
@Service
@Slf4j
public class WazuhService {

    @Autowired
    private WazuhDao wazuhDao;


    @Resource(name = "wazuhRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    private static String wazuhAlertIndexPrefix = "wazuh-alerts-4.x-";

    /**
     * 查询文件事件分页
     * @param wazuhFileEventRequest
     * @return ListObjResult<FileEventModel>
     */
    public ListObjResult<FileEventModel> queryFileEvents(WazuhFileEventRequest wazuhFileEventRequest){
        List<String> indexList = indexListBuilder(wazuhFileEventRequest);
        var indexArray = new String[indexList.size()];
        indexList.toArray(indexArray);
        var searchRequest = new SearchRequest(indexArray);

        var boolQueryBuilder = QueryBuilders.boolQuery();
        var rangeQueryBuilder = QueryBuilders.rangeQuery("timestamp")
            .from(wazuhFileEventRequest.getStartTime().toString()).to(wazuhFileEventRequest.getEndTime().toString());
        var sourceBuilder = new SearchSourceBuilder();

        //构造查询条件
        boolQueryBuilder.must(QueryBuilders.termQuery("rule.groups", wazuhFileEventRequest.getFileEventType().equals(FileEventType.REGISTRY)? "syscheck_registry": "syscheck"))
        .must(QueryBuilders.termQuery("agent.id", wazuhFileEventRequest.getAgentId()));

        searchRequest.source(sourceBuilder.query(boolQueryBuilder.must(rangeQueryBuilder)));
        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));

        //处理分页
        int  offset = (wazuhFileEventRequest.getPageNum() -1) * (wazuhFileEventRequest.getPageSize());
        sourceBuilder.from(offset);
        sourceBuilder.size(wazuhFileEventRequest.getPageSize());

        int total = queryTotalCount(indexArray, boolQueryBuilder);

        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<WazuhFileRoot> wazuhFileRootList = Arrays.stream(response.getHits().getHits()).map(hit -> JSONObject.parseObject(hit.getSourceAsString(), WazuhFileRoot.class)).collect(Collectors.toList());
            List<FileEventModel> fileEventModelList = wazuhFileRootList.stream().map(wazuhFileRoot -> FileEventModel.builder()
                .filePath(wazuhFileRoot.getSyscheck().getPath())
                .size(wazuhFileRoot.getSyscheck().getSizeAfter())
                .updateTime(LocalDateTimeUtils.dateToLocalDateTime(wazuhFileRoot.getSyscheck().getMtimeAfter()))
                .action(wazuhFileRoot.getSyscheck().getEvent())
                .build()).collect(Collectors.toList());
            return new ListObjResult<FileEventModel>(fileEventModelList, total);
        } catch (IOException e) {
            log.error("es 查询失败 {}", e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, e.toString());
        }
    }

    /**
     * 查询count
     * @param indexArray
     * @param boolQueryBuilder
     * @return count
     */
    private int queryTotalCount(String[] indexArray, BoolQueryBuilder boolQueryBuilder) {
        var countRequest = new CountRequest(indexArray);
        countRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        countRequest.query(boolQueryBuilder);
        var total = 0;
        try {
            CountResponse totalSize = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            total = (int) totalSize.getCount();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return total;
    }

    /**
     * 构造查询需要的索引
     *
     * @return List<IndexBoost>
     */
    @VisibleForTesting
    protected List<String> indexListBuilder(WazuhFileEventRequest wazuhFileEventRequest) {
        List<String> dateStringList = fetchFormatterTime(wazuhFileEventRequest.getStartTime(), wazuhFileEventRequest.getEndTime());
        return dateStringList.stream().map(dateString -> wazuhAlertIndexPrefix + dateString).collect(Collectors.toList());
    }

    /**
     * 获取比如2021-05-01 到2021-05-20.所有的日期，方便构建所有的时间字符串2021.05.01,2021.05.02,....
     *
     * @param startTime startTime
     * @param endTime endTime
     * @return List<String>
     */
    private List<String> fetchFormatterTime(LocalDateTime startTime, LocalDateTime endTime) {
        var formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        long betweenDay = ChronoUnit.DAYS.between(startTime, endTime);
        int days = (int) betweenDay;
        return IntStream.range(0, days + 1).boxed().map(startTime::plusDays).map((time) -> time.format(formatter))
            .collect(Collectors.toList());
    }
}
