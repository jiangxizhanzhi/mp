package cn.cyberpeace.mp.cscene.service;

import cn.hutool.core.util.NumberUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(SpringExtension.class)
class SceneMonitorServiceTest {

    private static Logger log = LoggerFactory.getLogger(SceneMonitorServiceTest.class);

    @Test
    void sceneInstanceBO2SceneMonitorInfo() {
        BigDecimal bigDecimal = NumberUtil.round(1024.12345, 4);
        log.info("" + bigDecimal.doubleValue());
    }
}
