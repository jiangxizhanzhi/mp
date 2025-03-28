package cn.cyberpeace.mp.auth.bo.sso;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @BelongsPackage: cn.cyberpeace.mp.auth.bo.sso
 * @Author: hhuang
 * @CreateTime: 2025/03/28  11:16
 * @Description: soc原始用户信息
 * @Version: 1.0
 */
@Data
public class SocUser {

    private AuthenticationSuccess authenticationSuccess;

    @Getter
    @Setter
    public static class AuthenticationSuccess {
        private String user;
        private Attributes attributes;
    }

    @Getter
    @Setter
    public static class Attributes {
        private Boolean isFromNewLogin;
        private String authenticationDate;
        private String successfulAuthenticationHandlers;
        private String credentialType;
        private String authenticationMethod;
        private Boolean longTermAuthenticationRequestTokenUsed;
        private String userId;
        private String realName;
        private String phone;
        private String roleCode;
        private String email;
        private String username;
        private String attributionScope;
    }
}
