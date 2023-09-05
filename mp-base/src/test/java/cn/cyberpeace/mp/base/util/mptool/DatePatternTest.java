package cn.cyberpeace.mp.base.util.mptool;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DatePatternTest {

    @Test
    void format() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println(DatePattern.NORM_DATETIME_FORMAT.format(now));
        System.out.println(DatePattern.UTC_SIMPLE_FORMAT.format(now));
    }

}