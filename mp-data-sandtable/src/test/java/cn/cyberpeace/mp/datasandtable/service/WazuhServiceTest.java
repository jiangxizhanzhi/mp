package cn.cyberpeace.mp.datasandtable.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.util.LocalDateTimeUtils;
import cn.cyberpeace.mp.datasandtable.TestDatasandtableApplciation;
import cn.cyberpeace.mp.datasandtable.model.FileEventModel;
import cn.cyberpeace.mp.datasandtable.model.WazuhEventRequest;
import cn.cyberpeace.mp.datasandtable.model.emum.FileEventType;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
import org.springframework.util.MultiValueMap;

@SpringBootTest(classes = TestDatasandtableApplciation.class)
@RunWith(SpringRunner.class)
public class WazuhServiceTest {

    @Autowired
    private WazuhService wazuhService;

    @Resource(name = "wazuhRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void testQueryFileEvents() {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        wazuhEventRequest.setMacIds(Lists.newArrayList("00:0c:29:22:d3:95"));
        wazuhEventRequest.setStartTime(LocalDateTime.now().minusDays(7));
        wazuhEventRequest.setEndTime(LocalDateTime.now());
        wazuhEventRequest.setPageNum(1);
        wazuhEventRequest.setPageSize(10);
        ListObjResult<FileEventModel> listObjResult =  wazuhService.queryFileEvents(wazuhEventRequest);
        Assert.isTrue(listObjResult.getObjs().size() >0, "查询文件监控分页有数据");
    }


    @Test
    public void testQueryWindowsRegistory() {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        wazuhEventRequest.setMacIds(Lists.newArrayList("30:D0:42:E0:84:CC"));
        wazuhEventRequest.setStartTime(LocalDateTime.now().minusDays(7));
        wazuhEventRequest.setEndTime(LocalDateTime.now());
        wazuhEventRequest.setPageNum(1);
        wazuhEventRequest.setPageSize(10);
        ListObjResult<FileEventModel> listObjResult =  wazuhService.queryFileEvents(wazuhEventRequest);
        Assert.isTrue(listObjResult.getObjs().get(0).getFileEventType().equals(FileEventType.REGISTRY), "查询文件监控分页有数据");
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
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        wazuhEventRequest.setStartTime(LocalDateTime.now().minusDays(7));
        wazuhEventRequest.setEndTime(LocalDateTime.now());
        List<String> indexList= wazuhService.indexListBuilder(wazuhEventRequest);
        System.out.println(indexList);
    }


    @Test
    public void testQueryAllAgentMacAddr() {
        MultiValueMap multiValueMap = wazuhService.queryAllAgentMacAddr();
        Assert.isTrue(Objects.nonNull(multiValueMap), "查询到mac的关系");
    }

    @Test
    public void testQueryMonitorEvents() {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        wazuhEventRequest.setMacIds(Lists.newArrayList("30:D0:42:E0:84:CC", "00:0c:29:22:d3:95"));
        wazuhEventRequest.setPageNum(1);
        wazuhEventRequest.setPageSize(10);
        wazuhEventRequest.setStartTime(LocalDateTime.now());
        wazuhEventRequest.setEndTime(LocalDateTime.now());
        wazuhService.queryMonitorEvents(wazuhEventRequest);
    }

    @Test
    public void testDateFormate() {
        String date = LocalDateTimeUtils.localDateTimeToString(LocalDateTime.now(), LocalDateTimeUtils.esdateTimeFormatter);
        System.out.println(date);
    }
}
