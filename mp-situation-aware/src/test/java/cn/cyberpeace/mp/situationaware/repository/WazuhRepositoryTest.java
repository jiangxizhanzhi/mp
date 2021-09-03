package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.situationaware.TestDatasandtableApplciation;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant;
import cn.cyberpeace.mp.situationaware.model.WazuhNetifaceRoot;
import cn.cyberpeace.mp.situationaware.model.WazuhRamRoot;
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
public class WazuhRepositoryTest {


    @Autowired
    private WazuhRepository wazuhRepository;

    @Test
    public void testWazuhToken() throws IOException, ExecutionException {
//        var token = wazuhDao.queryWazuhToken();
//        System.out.println("token" +  token);

        System.out.println(SituationalAwareConstant.wazuhToken.get("wazuhToken"));
        SituationalAwareConstant.wazuhToken.invalidate("wazuhToken");
        System.out.println(SituationalAwareConstant.wazuhToken.get("wazuhToken"));
                var token = wazuhRepository.queryWazuhToken();
    }

    @Test
    public void testWazuhRamRoot() throws IOException{
        WazuhRamRoot wazuhRamRoot = wazuhRepository.queryRamByAgentId("006");
        Assert.isTrue(Objects.nonNull(wazuhRamRoot.getData()), "error");
    }

    @Test
    public void testQueryFileEventsByAgentId() throws IOException{
//        WazuhFileRoot wazuhFileRoot = wazuhDao.queryFileEventsByAgentId("001");
//        Assert.isTrue(Objects.nonNull(wazuhFileRoot.getData()), "error");
    }

    @Test
    public void testQueryAllAgentMac() {
        WazuhNetifaceRoot wazuhNetifaceRoot = wazuhRepository.queryAllAgentMac();
        Assert.isTrue(Objects.nonNull(wazuhNetifaceRoot), "查询mac地址为空");
    }
}
