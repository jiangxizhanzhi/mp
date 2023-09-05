package cn.cyberpeace.mp.base.util.mptool;

import org.junit.jupiter.api.Test;

class Ipv4UtilTest {

    @Test
    void getMaskByMaskBit() {
        System.out.println(Ipv4Util.getMaskByMaskBit(24));
    }

    @Test
    void ipv4ToLong() {
        long l = Ipv4Util.ipv4ToLong("255.255.0.0");
        System.out.println(l);
        System.out.println(Ipv4Util.longToIpv4(l));
    }

    @Test
    void longToIpv4() {
    }

    @Test
    void list() {
        System.out.println(Ipv4Util.list("10.4.1.30/24",true));
    }

    @Test
    void testList() {
        System.out.println(Ipv4Util.list("10.4.1.30","10.4.1.50"));
    }


    @Test
    void getEndIpStr() {
        System.out.println(Ipv4Util.getEndIpStr("218.240.38.69",30));
    }

    @Test
    void getBeginIpStr() {
        System.out.println(Ipv4Util.getBeginIpStr("218.240.38.69",30));
    }
}