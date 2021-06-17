package cn.cyberpeace.mp.datasandtable.dao;

import cn.cyberpeace.mp.datasandtable.TestDatasandtableApplciation;
import cn.cyberpeace.mp.datasandtable.constant.DataSandtableConstant;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
}
