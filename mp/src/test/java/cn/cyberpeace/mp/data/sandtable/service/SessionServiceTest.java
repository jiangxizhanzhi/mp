package cn.cyberpeace.mp.data.sandtable.service;

import cn.cyberpeace.mp.Application;
import cn.cyberpeace.mp.data.sandtable.model.SessionQuery;
import java.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class SessionServiceTest {

    @Autowired
    private SessionService service;

    @Test
    public void testQuerySessionByGroup(){
        SessionQuery sessionQuery = new SessionQuery();
        sessionQuery.setStartTime(LocalDateTime.of(2021, 5, 21, 0, 0));
        sessionQuery.setEndTime(LocalDateTime.now());
        sessionQuery.setGroupId("default");
        service.querySessionListWithGroup(sessionQuery);
    };
}
