package cn.cyberpeace.mp.datasandtable.service;

import cn.cyberpeace.mp.datasandtable.TestDatasandtableApplciation;
import cn.cyberpeace.mp.datasandtable.model.SPIViewRequest;
import cn.cyberpeace.mp.datasandtable.model.SessionRequest;
import java.time.LocalDateTime;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = TestDatasandtableApplciation.class)
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
        sessionRequest.setProtocol("dns");
        service.querySessionListWithGroup(sessionRequest);
    }


    @Test
    public void testQuerySPIView(){
        SPIViewRequest spiViewRequest = new SPIViewRequest();
        spiViewRequest.setStartTime(1622678706L);
        spiViewRequest.setStopTime(1622682306L);
        spiViewRequest.setGroupId("9787c63b-d79f-4644-8027-b268f89aa8a9");
        String spires = service.querySPIView(spiViewRequest);
        System.out.println("spires: "+ spires);
    }
}
