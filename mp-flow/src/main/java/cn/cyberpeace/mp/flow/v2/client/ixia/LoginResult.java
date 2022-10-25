package cn.cyberpeace.mp.flow.v2.client.ixia;

import cn.cyberpeace.mp.base.util.http.HttpData;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/25
 */
public class LoginResult extends IxiaResponse implements HttpData {
    private String apiKey;
    private String sessionName;
    private String sessionId;
    private String username;
    private String userAccountUrl;
}
