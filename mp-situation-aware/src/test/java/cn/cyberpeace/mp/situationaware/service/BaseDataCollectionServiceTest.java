package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.base.util.es.EsConstants;
import cn.cyberpeace.mp.situationaware.model.FileEventModel;
import cn.cyberpeace.mp.situationaware.model.WazuhEventRequest;
import cn.cyberpeace.mp.situationaware.model.WazuhModel;
import cn.cyberpeace.mp.situationaware.model.WazuhMonitorEventsRequest;
import cn.cyberpeace.mp.situationaware.model.enums.FileEventType;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
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

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

//@SpringBootTest(classes = TestSituationAwareApplciation.class)
@RunWith(SpringRunner.class)
@SpringBootTest
//@ActiveProfiles("prod")
public class BaseDataCollectionServiceTest {

    @Autowired
    private DataCollectionService dataCollectionService;

    @Resource(name = "wazuhRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private DataCollectionFlowTableService dataCollectionFlowTableService;

    @Test
    public void testQueryFileEvents() {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        wazuhEventRequest.setMacIds(Lists.newArrayList("00:0c:29:86:21:ac"));
        wazuhEventRequest.setStartTime(LocalDateTime.now().minusDays(7));
        wazuhEventRequest.setEndTime(LocalDateTime.now());
        wazuhEventRequest.setPageNum(1);
        wazuhEventRequest.setPageSize(10);
        ListObjResult<FileEventModel> listObjResult =  dataCollectionService.queryFileEvents(wazuhEventRequest);
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
        ListObjResult<FileEventModel> listObjResult =  dataCollectionService.queryFileEvents(wazuhEventRequest);
        Assert.isTrue(listObjResult.getObjs().get(0).getFileEventType().equals(FileEventType.REGISTRY), "查询文件监控分页有数据");
    }

    @Test
    public void testWazuhESQuery() throws IOException {
        var searchRequest = new SearchRequest("wazuh-alerts-4.x-2021.07.*");
        var boolQueryBuilder = QueryBuilders.boolQuery();
        var sourceBuilder = new SearchSourceBuilder();
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));

        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    @Test
    public void indexListBuilder(){
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        wazuhEventRequest.setStartTime(LocalDateTime.now().minusMonths(7));
        wazuhEventRequest.setEndTime(LocalDateTime.now());
        List<String> indexList= dataCollectionService.indexListBuilder(LocalDateTime.now().minusDays(7), LocalDateTime.now());
        System.out.println(indexList);
    }


    @Test
    public void testQueryAllAgentMacAddr() {
//        MultiValueMap multiValueMap = dataCollectionService.queryAllAgentMacAddr();
//        Assert.isTrue(Objects.nonNull(multiValueMap), "查询到mac的关系");
    }

    @Test
    public void testQueryMonitorEvents() {
        WazuhMonitorEventsRequest wazuhEventRequest = new WazuhMonitorEventsRequest();
        wazuhEventRequest.setAgentNames(Lists.newArrayList("000e100b-ecce-41a4-a09c-0643e68d3eb6"));
        wazuhEventRequest.setPageNum(1);
        wazuhEventRequest.setPageSize(10);
        ListObjResult<WazuhModel> listObjResult= dataCollectionService.queryMonitorEvents(wazuhEventRequest);
        Assert.isTrue(listObjResult.getObjs().size() > 1 , "查询文件监控分页有数据");
    }

    @Test
    public void testDateFormate() {
        String date = TimeUtils.localDateTimeToString(LocalDateTime.now(), EsConstants.esDateTimeFormatter);
        System.out.println(date);
    }


    @Test
    public void  testQueryMonitorEventsWithTimeRange() {
        WazuhMonitorEventsRequest wazuhEventRequest = new WazuhMonitorEventsRequest();
        wazuhEventRequest.setAgentNames(Lists.newArrayList("ubuntu1", "windows_agent1"));
        wazuhEventRequest.setStartTime(LocalDateTime.now().minusDays(1));
        wazuhEventRequest.setEndTime(LocalDateTime.now());
        List<WazuhModel> wazuhModelList = dataCollectionService.queryMonitorEventsWithTimeRange(wazuhEventRequest);
        System.out.println(wazuhModelList);
    }

    @Test
    public void testCreateDataCollectionFlow() {
        dataCollectionFlowTableService.createDataCollectionFlow(Lists.newArrayList("84702684-9e92-4e44-a84d-990191cf2442"));
    }


    @Test
    public void testQueryMonitorEventsAgg() {
        WazuhMonitorEventsRequest wazuhEventRequest = new WazuhMonitorEventsRequest();
        wazuhEventRequest.setAgentNames(Lists.newArrayList("ubuntu2", "windows_agent1"));
        wazuhEventRequest.setStartTime(LocalDateTime.now().minusDays(1));
        wazuhEventRequest.setEndTime(LocalDateTime.now());
        dataCollectionService.queryMonitorEventsAgg(wazuhEventRequest);
    }

    @Test
    public void testGetJSon() {
        String address = "{\"addresses\":{\n" +
            "                \"1f01c2bd-042b-472e-a2de-d8af55374f9e\":[\n" +
            "                    {\n" +
            "                        \"addr\":\"192.168.1.78\",\n" +
            "                        \"version\":4,\n" +
            "                        \"port\":\"462abcff-9a26-46bb-8d13-49f22e6b2d9c\",\n" +
            "                        \"subnet_id\":\"40fbb744-05f2-4206-bbee-d8d64cf18f64\",\n" +
            "                        \"preserve_on_delete\":false\n" +
            "                    }\n" +
            "                ]\n" +
            "            }}";
        JSONObject jsonObject = JSONObject.parseObject(address);
        JSONObject addresses = (JSONObject) jsonObject.get("addresses");
        addresses.values().forEach((val)->{
            System.out.println(val);
        });

    }

    @Test
    public void testEventCount() {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        wazuhEventRequest.setAgentNames(Lists.newArrayList("ceea0967056e417193fb8883f27e7186", "46017f42971e403f88d17d51b07a74de", "aaaa"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        wazuhEventRequest.setStartTime(LocalDateTime.parse("2022-03-11 12:00:00", formatter));
        wazuhEventRequest.setEndTime(LocalDateTime.parse("2022-03-11 12:15:00", formatter));
        Map<String, Long> countMap = dataCollectionService.aggsCountByAgentNamesBucket(wazuhEventRequest);
        long count = dataCollectionService.countByAgentNames(wazuhEventRequest);
        System.out.println(count);
    }
}
