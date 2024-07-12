 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowActuatorServiceTest.java.
 // File size 845.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.flow.TestFlowApplication;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Description: 流量执行测试类
 * @Author qiaokang
 * @Date：2021/8/18 15:56
 */
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
