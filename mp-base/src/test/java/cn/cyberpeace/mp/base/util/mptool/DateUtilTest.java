package cn.cyberpeace.mp.base.util.mptool;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class DateUtilTest {

    @Test
    void format() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println(DateUtil.format(now,"yyyy-MM-dd HH:mm:ss"));
    }
}