package cn.cyberpeace.mp.datasandtable.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.datasandtable.TestDatasandtableApplciation;
import cn.cyberpeace.mp.datasandtable.model.FileEventModel;
import cn.cyberpeace.mp.datasandtable.model.WazuhFileEventRequest;
import cn.cyberpeace.mp.datasandtable.model.emum.FileEventType;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import javax.annotation.Resource;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

@SpringBootTest(classes = TestDatasandtableApplciation.class)
@RunWith(SpringRunner.class)
public class WazuhServiceTest {

    @Autowired
    private WazuhService wazuhService;

    @Resource(name = "wazuhRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void testQueryFileEvents() {
        WazuhFileEventRequest wazuhFileEventRequest = new WazuhFileEventRequest();
        wazuhFileEventRequest.setAgentIdList(Lists.newArrayList("004"));
        wazuhFileEventRequest.setStartTime(LocalDateTime.now().minusDays(7));
        wazuhFileEventRequest.setEndTime(LocalDateTime.now());
        wazuhFileEventRequest.setPageNum(1);
        wazuhFileEventRequest.setPageSize(10);
        ListObjResult<FileEventModel> listObjResult =  wazuhService.queryFileEvents(wazuhFileEventRequest);
        Assert.isTrue(listObjResult.getObjs().size() >0, "查询文件监控分页有数据");
    }

    @Test
    public void testWazuhESQuery() throws IOException {
        var searchRequest = new SearchRequest("wazuh-alerts-4.x-2021.06.18");
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));

        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    @Test
    public void indexListBuilder(){
        WazuhFileEventRequest wazuhFileEventRequest = new WazuhFileEventRequest();
        wazuhFileEventRequest.setStartTime(LocalDateTime.now().minusDays(7));
        wazuhFileEventRequest.setEndTime(LocalDateTime.now());
        List<String> indexList= wazuhService.indexListBuilder(wazuhFileEventRequest);
        System.out.println(indexList);
    }


}
