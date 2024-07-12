 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AgentServiceTest.java.
 // File size 581.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
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
