package cn.cyberpeace.mp.datasandtable.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.datasandtable.dao.WazuhDao;
import cn.cyberpeace.mp.datasandtable.model.FileEventModel;
import cn.cyberpeace.mp.datasandtable.model.SessionRequest;
import cn.cyberpeace.mp.datasandtable.model.WazuhFileEventRequest;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Resource;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * wazuh对接
 */
@Service
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
        searchRequest.source(sourceBuilder.query(boolQueryBuilder.must(rangeQueryBuilder)));


        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
