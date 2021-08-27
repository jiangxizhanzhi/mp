package cn.cyberpeace.mp.datasandtable.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.util.LocalDateTimeUtils;
import cn.cyberpeace.mp.datasandtable.TestDatasandtableApplciation;
import cn.cyberpeace.mp.datasandtable.model.FileEventModel;
import cn.cyberpeace.mp.datasandtable.model.WazuhEventRequest;
import cn.cyberpeace.mp.datasandtable.model.WazuhModel;
import cn.cyberpeace.mp.datasandtable.model.WazuhMonitorEventsRequest;
import cn.cyberpeace.mp.datasandtable.model.enums.FileEventType;
import com.alibaba.fastjson.JSONObject;
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
public class DataCollectionServiceTest {

    @Autowired
    private DataCollectionService dataCollectionService;

    @Resource(name = "wazuhRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

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
        MultiValueMap multiValueMap = dataCollectionService.queryAllAgentMacAddr();
        Assert.isTrue(Objects.nonNull(multiValueMap), "查询到mac的关系");
    }

    @Test
    public void testQueryMonitorEvents() {
        WazuhMonitorEventsRequest wazuhEventRequest = new WazuhMonitorEventsRequest();
        wazuhEventRequest.setMacIds(Lists.newArrayList("30:D0:42:E0:84:CC", "00:0c:29:22:d3:95"));
        wazuhEventRequest.setPageNum(1);
        wazuhEventRequest.setPageSize(10);
        ListObjResult<WazuhModel> listObjResult= dataCollectionService.queryMonitorEvents(wazuhEventRequest);
        Assert.isTrue(listObjResult.getObjs().size() > 1 , "查询文件监控分页有数据");
    }

    @Test
    public void testDateFormate() {
        String date = LocalDateTimeUtils.localDateTimeToString(LocalDateTime.now(), LocalDateTimeUtils.esdateTimeFormatter);
        System.out.println(date);
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
}
