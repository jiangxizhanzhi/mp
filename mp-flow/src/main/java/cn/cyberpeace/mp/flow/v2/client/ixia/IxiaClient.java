package cn.cyberpeace.mp.flow.v2.client.ixia;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/25
 */
public class IxiaClient {
    private IxiaHttpClient ixiaHttpClient = new IxiaHttpClient("http://10.100.7.199", 6000, 10);

    public LoginResult login(String username, String password) {
        LoginData loginData = new LoginData();
        loginData.setUsername(username);
        loginData.setPassword(password);
        var result = ixiaHttpClient.ixiaPost(IxiaUrlConstant.LOGIN_URL, loginData, null, new TypeReference<LoginResult>() {
        });
        return result;
    }
}
