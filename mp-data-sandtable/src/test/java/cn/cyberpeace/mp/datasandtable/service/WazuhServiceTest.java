package cn.cyberpeace.mp.datasandtable.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.datasandtable.TestDatasandtableApplciation;
import cn.cyberpeace.mp.datasandtable.model.FileEventModel;
import cn.cyberpeace.mp.datasandtable.model.WazuhFileEventRequest;
import java.time.LocalDateTime;
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

    @Test
    public void testQueryFileEvents() {
        WazuhFileEventRequest wazuhFileEventRequest = new WazuhFileEventRequest();
        wazuhFileEventRequest.setAgentId("004");
        wazuhFileEventRequest.setStartTime(LocalDateTime.now().minusDays(1));
        wazuhFileEventRequest.setEndTime(LocalDateTime.now());
        wazuhFileEventRequest.setPageNum(1);
        wazuhFileEventRequest.setPageSize(20);
        ListObjResult<FileEventModel> listObjResult=  wazuhService.queryFileEvents(wazuhFileEventRequest);
        Assert.isTrue(listObjResult.getObjs().size() >0, "查询文件监控分页有数据");
    }
}
