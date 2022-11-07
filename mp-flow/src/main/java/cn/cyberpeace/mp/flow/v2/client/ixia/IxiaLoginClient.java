package cn.cyberpeace.mp.flow.v2.client.ixia;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/25
 */
@Slf4j
public class IxiaLoginClient {
    private String username;
    private String password;

    private IxiaHttpClient ixiaHttpClient;

    public IxiaLoginClient(IxiaHttpClient ixiaHttpClient, String username, String password) {
        this.ixiaHttpClient = ixiaHttpClient;
        this.username = username;
        this.password = password;
    }

    public LoginResult login() {
        LoginData loginData = new LoginData();
        loginData.setUsername(username);
        loginData.setPassword(password);
        var result = ixiaHttpClient.ixiaPost(IxiaUrlConstant.LOGIN_URL, loginData, null, LoginResult.class, new TypeReference<LoginResult>() {
        });
        log.info("Login ixia response:{}", result);
        return result;
    }

    public void logout() {
        var result = ixiaHttpClient.ixiaDelete(IxiaUrlConstant.LOGIN_URL, null, null, IxiaResponse.class, new TypeReference<IxiaResponse>() {
        });
        log.info("Login out ixia response:{}", result);
    }

}
