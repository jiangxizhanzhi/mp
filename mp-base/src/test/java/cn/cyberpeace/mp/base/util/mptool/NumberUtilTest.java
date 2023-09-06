package cn.cyberpeace.mp.base.util.mptool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumberUtilTest {

    @Test
    void testEquals() {
        System.out.println(NumberUtil.equals(0.00f,0));
    }

    @Test
    void isNumber() {
    }

    @Test
    void round() {
        System.out.println(NumberUtil.round(123.456789,2));
    }
}