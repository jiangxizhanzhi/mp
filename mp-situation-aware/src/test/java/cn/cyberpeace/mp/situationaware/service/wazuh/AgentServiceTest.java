package cn.cyberpeace.mp.situationaware.service.wazuh;

import cn.cyberpeace.mp.situationaware.TestSituationAwareApplciation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = TestSituationAwareApplciation.class)
public class AgentServiceTest {

    @Autowired
    private AgentService agentService;

    @Test
    public void testListAgents() {
//        agentService.listAgents(new AgentRequest("windows_agent1"));
    }

}
