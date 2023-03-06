package cn.cyberpeace.mp.log.service;

import cn.cyberpeace.mp.log.LogApplcation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
  *@Desciption
  *@Author zhangjin
  *@Date 9/13/21 10:55 上午
**/
@SpringBootTest(classes = LogApplcation.class)
public class RuntimeLogServiceTest {

    @Autowired
    private RuntimeLogService runtimeLogService;

    @Test
    public void testQueryRuntimeLog() {
//        runtimeLogService.queryRuntimeLog(new BasePageRequest());
    }
}
