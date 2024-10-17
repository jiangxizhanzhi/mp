package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.extensions.security.filter.JwtAuthenticationFilter;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.request.user.UserRequest;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.HttpUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ThirdSsoService {


    @Resource
    private AuthConfig authConfig;

    @Resource
    private UserService userService;


    public void loginByToken(String code, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String clientId = "";
        String clientSecret = "";
        String getTokenUrl = "http://127.0.0.1:8080/api/v1/sso/token";
        String getUserInfoUrl = "http://127.0.0.1:8080/api/v1/sso/getuserinfo";
        try {
            GetTokenParams getTokenParams = new GetTokenParams();
            getTokenParams.setCode(code);
            getTokenParams.setClientId(clientId);
            getTokenParams.setClientSecret(clientSecret);
            String params = JsonCommon.snakeStringify(getTokenParams);
            log.info("query third sso token , url is {} , params is {}", getTokenUrl, params);
            String result = HttpUtils.sendPost(getTokenUrl, params);
            log.info("query third sso token , result is {}", result);

            ThirdResponse<ThirdToken> tokenResponse = JsonCommon.snakeParse(result, new TypeReference<ThirdResponse<ThirdToken>>() {
            });

            // 查询用户信息
            var thirdToken = tokenResponse.getData();
            GetUserinfoParams getUserinfoParams = new GetUserinfoParams();
            getUserinfoParams.setAccessToken(thirdToken.getAccessToken());
            getUserinfoParams.setClientId(clientId);
            params = JsonCommon.snakeStringify(getUserinfoParams);
            log.info("query user info , url is {} , params is {}", getUserInfoUrl, params);
            result = HttpUtils.sendPost(getUserInfoUrl, params);
            log.info("query user info , result is {}", result);

            ThirdResponse<ThirdUserInfo> userResponse = JsonCommon.snakeParse(result, new TypeReference<ThirdResponse<ThirdUserInfo>>() {
            });
            var userInfo = userResponse.getData();
            UserModel userModel = userService.getUserModelById(userInfo.getUserAccount());
            if (Objects.isNull(userModel)) {
                // 新增一个用户
                UserRequest userRequest = new UserRequest();
                userRequest.setPassword("");
                userRequest.setAvatar("");
                userRequest.setIdentifier(userInfo.getUserAccount());
                userRequest.setDescription("");
                userRequest.setTokenSecret("");
                userRequest.setUsername(userInfo.getUserAccount());
                userRequest.setNickname(userInfo.getUserName());
                userRequest.setEmail(userInfo.getUserEmail());
                userRequest.setTenant("default");
                userService.add(userRequest);
            }
            String newJwt = JwtUtils.generateToken(new JwtUtils.UserInfo(userModel.getId(), userModel.getUsername(), userModel.getTokenSecret()));
            JwtAuthenticationFilter.addJWTCookieToResponse(httpServletResponse, newJwt);
            JwtAuthenticationFilter.addThirdTokenToResponse(httpServletResponse, thirdToken.getLoginToken());
        } catch (Exception e) {
            log.error("faild to loginByToken ", e);
            throw new RuntimeException(e);
        }
    }

    public boolean tokenCheck(String token) {
        String clientId = "";
        String tokenCheckUrl = "http://127.0.0.1:8080/api/v1/sso/logintoken";
        TokenCheckParams tokenCheckParams = new TokenCheckParams();
        tokenCheckParams.setClientId(clientId);
        tokenCheckParams.setLoginToken(token);
        String params = JsonCommon.snakeStringify(tokenCheckParams);

        log.info("check token , url is {} , params is {}", tokenCheckUrl, params);
        String result = null;
        try {
            result = HttpUtils.sendPost(tokenCheckUrl, params);
            log.info("check token , result is {}", result);
        } catch (IOException e) {
            log.error("faild to tokenCheck ", e);
            throw new RuntimeException(e);
        }
        ThirdResponse<TokenCheckResult> response = JsonCommon.snakeParse(result, new TypeReference<ThirdResponse<TokenCheckResult>>() {
        });
        if (response.getCode() == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Getter
    @Setter
    private static class GetTokenParams {
        private String code;
        private String clientId;
        private String clientSecret;
    }

    @Data
    public static class ThirdResponse<T> {
        private T data;

        private Integer code;

        private String msg;
    }

    @Getter
    @Setter
    public static class ThirdToken {
        private String accessToken;
        private String tokenType;
        private String expiresIn;
        private String refreshToken;
        private String scope;
        private String loginToken;
    }

    @Getter
    @Setter
    public static class ThirdUserInfo {
        private String userAccount;
        private List<String> userRole;
        private String userName;
        private String userSex;
        private String status;
        private String header;
        private Integer organizeId;
        private String userEmail;
        private Long createTime;
    }

    @Getter
    @Setter
    private static class GetUserinfoParams {
        private String accessToken;
        private String clientId;
    }

    @Getter
    @Setter
    private static class TokenCheckParams {
        private String loginToken;
        private String clientId;
    }

    @Getter
    @Setter
    private static class TokenCheckResult {
        private Long loginTimeout;
        private String userAccount;
    }

}
