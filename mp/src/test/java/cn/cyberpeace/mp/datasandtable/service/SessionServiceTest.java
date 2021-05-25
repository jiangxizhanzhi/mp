package cn.cyberpeace.mp.datasandtable.service;

import cn.cyberpeace.mp.Application;
import cn.cyberpeace.mp.datasandtable.model.SessionRequest;
import java.time.LocalDateTime;
import org.assertj.core.util.Lists;
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
        SessionRequest sessionRequest = new SessionRequest();
        sessionRequest.setStartTime(LocalDateTime.of(2021, 5, 21, 0, 0));
        sessionRequest.setEndTime(LocalDateTime.now());
        sessionRequest.setGroupList(Lists.newArrayList("default"));
        service.querySessionListWithGroup(sessionRequest);
    };

    @Test
    public void testQuerySessionBySourceIp(){
        SessionRequest sessionRequest = new SessionRequest();
        sessionRequest.setStartTime(LocalDateTime.of(2021, 5, 21, 0, 0));
        sessionRequest.setEndTime(LocalDateTime.now());
        sessionRequest.setGroupList(Lists.newArrayList("default"));
        sessionRequest.setSourceIp("10.0.1.253");
        service.querySessionListWithGroup(sessionRequest);
    };
}
