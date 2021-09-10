package cn.cyberpeace.mp.base.util.http;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

/**
 * @Description
 * @Author maojun
 * @Date 2021/9/8
 */
public class HttpTest {
    public static void main(String[] args) {
        User user = new User();
        user.setName("name");
        user.setPassword("password");
        user.setTest_test_test_a("a");
        user.setTestTestTestB("b");

        // 构建header
        MpHttpHeaders.builder()
                // 特定的header
                .addAuthorization("test")
                .addContentType("test")
                // 自定义的header
                .add("test", "test")
                .build();

        // 通过对象 构建body key转成蛇形写法
        MpHttpReqBodyBuilder.builderFor(user, ParseMode.SNAKE_JSON)
                // 排除user特定的属性
                .addExcludeFields(User::getName, User::getPassword)
                // 选取user特定的属性
                .addIncludeFields(User::getName, User::getPassword)
                .build();
        // 通过key value 构建body key转成蛇形写法
        MpHttpReqBodyBuilder.builderForMap()
                .addKeyValue("test2", "test2")
                .addKeyValues(new HashMap<>())
                .build();

        // 通过对象构建 url参数
        MpHttpUrlParamsBuilder.builderForObj(user)
                .addExcludeFields(User::getName, User::getPassword)
                .addIncludeFields(User::getName, User::getPassword)
                .build();
        MpHttpUrlParamsBuilder.builderForMap()
                .addKeyValue("test1", "test1")
                .addKeyValue("test2", "test2")
                .build();

        // 最终的写法
        MpHttpArgs params = MpHttpArgs.builder()
                .setHeaders(MpHttpHeaders.builder()
                        .addContentType("json")
                        .addAuthorization("asdfasdfas")
                        .build())
                .setBodyContent(MpHttpReqBodyBuilder.builderFor(user)
                        .addExcludeFields(User::getName, User::getPassword)
                        .addIncludeFields(User::getName, User::getPassword)
                        .build())
                .build();
        String x1 = HttpUtilsV2.sendPost("http://127.0.0.1:8090/test/test2.do", params, String.class);
        User x2 = HttpUtilsV2.sendPost("http://127.0.0.1:8090/test/test2.do", params, User.class);
        JSONObject x3 = HttpUtilsV2.sendPost("http://127.0.0.1:8090/test/test2.do", params, JSONObject.class);
    }
}

class User {
    private String name;
    private String password;
    private String test_test_test_a;
    private String testTestTestB;
    private User user;

    public String getName() {
        return "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTest_test_test_a() {
        return test_test_test_a;
    }

    public void setTest_test_test_a(String test_test_test_a) {
        this.test_test_test_a = test_test_test_a;
    }

    public String getTestTestTestB() {
        return testTestTestB;
    }

    public void setTestTestTestB(String testTestTestB) {
        this.testTestTestB = testTestTestB;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
