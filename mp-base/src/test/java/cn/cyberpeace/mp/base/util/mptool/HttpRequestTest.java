package cn.cyberpeace.mp.base.util.mptool;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestTest {

    @Test
    void sendGetRequest() {
        System.out.println(HttpRequest.sendGetRequest("http://www.baidu.com",new HashMap<>()));
    }
}