package cn.cyberpeace.mp.situationaware.service.wazuh;

import cn.cyberpeace.mp.situationaware.TestSituationAwareApplciation;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.request.AgentRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = TestSituationAwareApplciation.class)
@RunWith(SpringRunner.class)
public class AgentServiceTest {

    @Autowired
    private AgentService agentService;

    @Test
    public void testListAgents() {
        agentService.listAgents(new AgentRequest("windows_agent1"));
    }

}
