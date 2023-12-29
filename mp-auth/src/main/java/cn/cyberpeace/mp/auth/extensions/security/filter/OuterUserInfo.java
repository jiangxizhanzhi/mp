package cn.cyberpeace.mp.auth.extensions.security.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OuterUserInfo {
    private Integer code;
    private String accessToken;
    private String un;
    private String dis;
    private String msg;
}
