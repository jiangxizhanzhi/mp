package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.flow.TestFlowApplication;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description: 流量执行测试类
 * @Author qiaokang
 * @Date：2021/8/18 15:56
 */
@SpringBootTest(classes = TestFlowApplication.class)
@RunWith(SpringRunner.class)
public class FlowActuatorServiceTest {

    @Autowired
    private FlowActuatorMapper flowActuatorMapper;

    @Test
    public void testSelectById() {
        FlowActuatorModel flowActuatorModel = flowActuatorMapper.get("aaa");
        System.out.println("================>" + flowActuatorModel);
    }

}
