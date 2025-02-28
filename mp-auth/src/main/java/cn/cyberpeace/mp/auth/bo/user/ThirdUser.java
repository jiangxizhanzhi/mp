package cn.cyberpeace.mp.auth.bo.user;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ThirdUser {

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
