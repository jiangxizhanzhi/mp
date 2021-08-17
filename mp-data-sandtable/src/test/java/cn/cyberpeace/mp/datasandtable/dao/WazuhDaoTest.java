package cn.cyberpeace.mp.datasandtable.dao;

import cn.cyberpeace.mp.datasandtable.TestDatasandtableApplciation;
import cn.cyberpeace.mp.datasandtable.constant.DataSandtableConstant;
import cn.cyberpeace.mp.datasandtable.model.WazuhFileRoot;
import cn.cyberpeace.mp.datasandtable.model.WazuhNetifaceRoot;
import cn.cyberpeace.mp.datasandtable.model.WazuhRamRoot;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

@SpringBootTest(classes = TestDatasandtableApplciation.class)
@RunWith(SpringRunner.class)
public class WazuhDaoTest {


    @Autowired
    private WazuhDao wazuhDao;

    @Test
    public void testWazuhToken() throws IOException, ExecutionException {
//        var token = wazuhDao.queryWazuhToken();
//        System.out.println("token" +  token);

        System.out.println(DataSandtableConstant.wazuhToken.get("wazuhToken"));
        DataSandtableConstant.wazuhToken.invalidate("wazuhToken");
        System.out.println(DataSandtableConstant.wazuhToken.get("wazuhToken"));
                var token = wazuhDao.queryWazuhToken();
    }

    @Test
    public void testWazuhRamRoot() throws IOException{
        WazuhRamRoot wazuhRamRoot = wazuhDao.queryRamByAgentId("006");
        Assert.isTrue(Objects.nonNull(wazuhRamRoot.getData()), "error");
    }

    @Test
    public void testQueryFileEventsByAgentId() throws IOException{
//        WazuhFileRoot wazuhFileRoot = wazuhDao.queryFileEventsByAgentId("001");
//        Assert.isTrue(Objects.nonNull(wazuhFileRoot.getData()), "error");
    }

    @Test
    public void testQueryAllAgentMac() {
        WazuhNetifaceRoot wazuhNetifaceRoot = wazuhDao.queryAllAgentMac();
        Assert.isTrue(Objects.nonNull(wazuhNetifaceRoot), "查询mac地址为空");
    }
}
