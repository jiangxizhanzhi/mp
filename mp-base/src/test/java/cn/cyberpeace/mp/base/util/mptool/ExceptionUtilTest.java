package cn.cyberpeace.mp.base.util.mptool;

import org.junit.jupiter.api.Test;

class ExceptionUtilTest {

    @Test
    void stacktraceToString() {
        try {
            int i = 1 / 0;
        } catch (Exception e) {
            System.out.println(ExceptionUtil.stacktraceToString(e));
        }

    }
}