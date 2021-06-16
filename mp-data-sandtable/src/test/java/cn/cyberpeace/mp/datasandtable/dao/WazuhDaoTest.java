package cn.cyberpeace.mp.datasandtable.dao;

import cn.cyberpeace.mp.datasandtable.TestDatasandtableApplciation;
import java.io.IOException;
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
    public void testWazuhToken() throws IOException {
        var token = wazuhDao.queryWazuhToken();
        System.out.println("token" +  token);
    }
}
