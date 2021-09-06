package cn.cyberpeace.mp.situationaware.task;

import cn.cyberpeace.mp.situationaware.TestSituationAwareApplciation;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest(classes = TestSituationAwareApplciation.class)
@RunWith(SpringRunner.class)
public class ProbeManagerTaskTest {

    @Autowired
    private ProbeManagerTask probeManagerTask;

    public void testQueryProbeExist() {

    }
}
