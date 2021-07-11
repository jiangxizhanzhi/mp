package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.flow.TestFlowApplication;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(classes = TestFlowApplication.class)
public class FlowActuatorServiceTest {

    @Autowired
    private FlowActuatorMapper flowActuatorMapper;

    @Test
    public void testSelectById() {
        FlowActuatorModel flowActuatorModel = flowActuatorMapper.get("aaa");
        System.out.println("================>" + flowActuatorModel);
    }

}
