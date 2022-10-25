package cn.cyberpeace.mp.flow.v2.client.ixia;

import cn.cyberpeace.mp.base.util.http.HttpData;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/25
 */
@Data
public class LoginResult extends IxiaResponse {
    private String apiKey;
    private String sessionName;
    private String sessionId;
    private String username;
    private String userAccountUrl;
}
