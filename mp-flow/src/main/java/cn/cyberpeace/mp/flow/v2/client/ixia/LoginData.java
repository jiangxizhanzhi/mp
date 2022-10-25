package cn.cyberpeace.mp.flow.v2.client.ixia;

import cn.cyberpeace.mp.base.util.http.SnakeHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginData implements SnakeHttpData {
    private String username;
    private String password;
}
