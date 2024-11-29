package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.config.ThirdSsoConfig;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.extensions.security.filter.JwtAuthenticationFilter;
import cn.cyberpeace.mp.auth.model.TokenModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.TokenMapper;
import cn.cyberpeace.mp.auth.repository.UserMapper;
import cn.cyberpeace.mp.auth.request.user.UserRequest;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.HttpUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static cn.cyberpeace.mp.auth.constant.AuthConstant.ROLE_ADMIN_ID;
import static cn.cyberpeace.mp.auth.constant.AuthConstant.ROOT_ORG_ID;

@Slf4j
@Service
public class GuanWangAuthServiceImpl {

    @Resource
    private ThirdSsoConfig thirdSsoConfig;

    @Resource
    private UserService userService;

    @Resource
    private TokenMapper tokenMapper;

    public String loginByCode(String code, HttpServletRequest request, HttpServletResponse response) {
        if (code == null) {
            String getCodeUrl = thirdSsoConfig.getGetcodeurl();
            return String.format("redirect:%s", getCodeUrl);
        } else {
            GetTokenRes getTokenRes = this.getToken(code, request, response);
            ThirdUser userInfo = this.getThirdUser(getTokenRes.getAccessToken());
            UserModel userModel = userService.getByUserName(userInfo.getAccount_no());
            if (Objects.isNull(userModel)) {
                // 新增一个用户
                userModel = new UserModel();
                userModel.setUsername(userInfo.getAccount_no());
                userModel.setIdentifier(userInfo.getAccount_no());
                userModel.setAvatar("");
                userModel.setEmail("");
                userModel.setMobile("");
                userModel.setAddress("");
                userModel.initDefault();
                BeanCommon.getBean(UserMapper.class).save(userModel);
            }
            String newJwt = JwtUtils.generateToken(new JwtUtils.UserInfo(userModel.getId(), userModel.getUsername(), userModel.getTokenSecret()));
            JwtAuthenticationFilter.addJWTCookieToResponse(response, newJwt);
            response.setHeader(AuthConstant.JWT_HEADER_KEY, newJwt);
            response.setHeader(AuthConstant.CP_TOKEN_REFRESHED_HEADER_KEY, "true");
            JwtAuthenticationFilter.addThirdTokenToResponse(response, getTokenRes.getAccessToken());
            TokenModel tokenModel = new TokenModel();
            tokenModel.setUserName(userModel.getUsername());
            tokenModel.setAccessToken(tokenModel.getAccessToken());
            tokenModel.setJwt(newJwt);
            tokenModel.setCreateTime(LocalDateTime.now());
            log.info("tokeModel:[{}]", tokenModel);
            tokenMapper.create(tokenModel);
        }
        return String.format("redirect:%s", thirdSsoConfig.getPageurl());
    }

    public GetTokenRes getToken(String code, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        GetTokenReq req = new GetTokenReq();
        req.setCode(code);
        req.setClientId(thirdSsoConfig.getClientid());
        req.setGrantType("authorization_code");
        req.setOauthTimestamp(String.valueOf(System.currentTimeMillis()));
        req.setClientSecret(thirdSsoConfig.getClientsecret());
        req.setRedirectUri(URLEncoder.encode("http://www.cyberpeacebc.cn/api/mp/unauth/third-login", Charset.forName("utf-8")));
        String bodyContent = JsonCommon.stringify(req);

        String url = thirdSsoConfig.getGettokenurl();
        url = url + "?client_id=" + thirdSsoConfig.getClientid();
        url = url + "&client_secret=" + thirdSsoConfig.getClientsecret();
        url = url + "&grant_type=authorization_code";
        url = url + "&redirect_uri=" + URLEncoder.encode("http://www.cyberpeacebc.cn/api/mp/unauth/third-login", Charset.forName("utf-8"));
        url = url + "&oauth_timestamp=" + System.currentTimeMillis();
        url = url + "&code=" + code;
        log.info("查询access_token , url is {} , body content is {}", url, bodyContent);
        try {
            String result = HttpUtils.sendPost(url, JsonCommon.snakeStringify(req));
            log.info("result is : {}", result);
            GetTokenRes res = JsonCommon.snakeParse(result, GetTokenRes.class);
            return res;
        } catch (Exception e) {
            log.error("failed to query token ", e);
            throw new RuntimeException(e);
        }
    }

    public ThirdUser getThirdUser(String accessToken) {
        String url = thirdSsoConfig.getGetuserurl() + "?access_token=" + accessToken;
        try {
            log.info("查询用户信息,url is {}", url);
            var result = HttpUtils.sendGet(url);
            log.info("result is : {}", result);
            Map<String, Object> data = JsonCommon.parseMap(result);
            ThirdUser thirdUser = JsonCommon.snakeParse(JsonCommon.snakeStringify(data.get("attributes")), ThirdUser.class);
            return thirdUser;
        } catch (IOException e) {
            log.error("failed to get user ", e);
            throw new RuntimeException(e);
        }
    }

    public boolean tokenCheck(String accessToken) {
        var user = this.getThirdUser(accessToken);
        if (user == null) {
            return false;
        }
        if (System.currentTimeMillis() > user.getTokenExpired()) {
            return false;
        }
        return true;
    }

    @Getter
    @Setter
    public static class ThirdUser {
        private String userName;
        private Long tokenGtime;
        private Long tokenExpired;
        private String account_no;
    }

    @Getter
    @Setter
    public static class GetTokenReq {
        private String clientId;
        private String grantType;
        private String oauthTimestamp;
        private String clientSecret;
        private String redirectUri;
        private String code;
    }

    @Getter
    @Setter
    public static class GetTokenRes {
        private String accessToken;
        private String tokenType;
        private String expiresIn;
    }
}
