package cn.cyberpeace.mp.devops.log.service;

import cn.cyberpeace.mp.devops.log.DevopslogApplcation;
import cn.cyberpeace.mp.devops.log.request.BasePageRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
  *@Desciption
  *@Author zhangjin
  *@Date 9/13/21 10:55 上午
**/
@SpringBootTest(classes = DevopslogApplcation.class)
@RunWith(SpringRunner.class)
public class RuntimeLogServiceTest {

    @Autowired
    private RuntimeLogService runtimeLogService;

    @Test
    public void testQueryRuntimeLog() {
        runtimeLogService.queryRuntimeLog(new BasePageRequest());
    }
}
