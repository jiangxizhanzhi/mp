 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneMonitorServiceTest.java.
 // File size 697.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.util.mptool.NumberUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

@ExtendWith(SpringExtension.class)
class SceneMonitorServiceTest {

    private static Logger log = LoggerFactory.getLogger(SceneMonitorServiceTest.class);

    @Test
    void sceneInstanceBO2SceneMonitorInfo() {
        BigDecimal bigDecimal = NumberUtil.round(1024.12345, 4);
        log.info("" + bigDecimal.doubleValue());
    }
}
