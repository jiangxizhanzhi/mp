package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.config.ThirdSsoConfig;
import cn.cyberpeace.mp.auth.extensions.security.filter.JwtAuthenticationFilter;
import cn.cyberpeace.mp.auth.model.TokenModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.TokenMapper;
import cn.cyberpeace.mp.auth.request.user.UserRequest;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.JwtUtils;
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
            String getCodeUrl = thirdSsoConfig.getGetCodeUrl();
            return String.format("redirect:%s", getCodeUrl);
        } else {
            GetTokenRes getTokenRes = this.getToken(code, request, response);
            ThirdUser userInfo = this.getThirdUser(getTokenRes.getAccessToken());
            UserModel userModel = userService.getByUserName(userInfo.getAccount_no());
            if (Objects.isNull(userModel)) {
                // 新增一个用户
                UserRequest userRequest = new UserRequest();
                userRequest.setUsername(userInfo.getAccount_no());
                userRequest.setName(userInfo.getUserName());
                userRequest.setRoleIds(Collections.singletonList(ROLE_ADMIN_ID));
                userRequest.setOrgIds(Collections.singletonList(ROOT_ORG_ID));
                userRequest.setPassword("d8165e80c4abed2dcc1dd06df1a8be31");
                // todo 处理默认的角色关系等
                userModel = userService.add(userRequest);
            }
            String newJwt = JwtUtils.generateToken(new JwtUtils.UserInfo(userModel.getId(), userModel.getUsername(), userModel.getTokenSecret()));
            JwtAuthenticationFilter.addJWTCookieToResponse(response, newJwt);
            JwtAuthenticationFilter.addThirdTokenToResponse(response, getTokenRes.getAccessToken());
            TokenModel tokenModel = new TokenModel();
            tokenModel.setUserName(userModel.getUsername());
            tokenModel.setAccessToken(tokenModel.getAccessToken());
            tokenModel.setJwt(newJwt);
            tokenModel.setCreateTime(LocalDateTime.now());
            log.info("tokeModel:[{}]", tokenModel);
            tokenMapper.create(tokenModel);
        }
        return String.format("redirect:%s", thirdSsoConfig.getPageUrl());
    }

    public GetTokenRes getToken(String code, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        GetTokenReq req = new GetTokenReq();
        req.setCode(code);
        req.setClientId(thirdSsoConfig.getClientId());
        req.setGrantType("authorization_code");
        req.setOauthTimestamp(String.valueOf(System.currentTimeMillis()));
        req.setClientSecret(thirdSsoConfig.getClientSecret());
        req.setRedirectUri(URLEncoder.encode(thirdSsoConfig.getRedirectUri(), Charset.forName("utf-8")));
        String bodyContent = JsonCommon.stringify(req);
        log.info("body content is : {}", bodyContent);
        try {
            String result = HttpUtils.sendPost(thirdSsoConfig.getGetTokenUrl(), JsonCommon.snakeStringify(req));
            log.info("result is : {}", result);
            GetTokenRes res = JsonCommon.snakeParse(result, GetTokenRes.class);
            return res;
        } catch (Exception e) {
            log.error("failed to query token ", e);
            throw new RuntimeException(e);
        }
    }

    public ThirdUser getThirdUser(String accessToken) {
        String url = thirdSsoConfig.getGetUserUrl() + "?access_token=" + accessToken;
        try {
            var result = HttpUtils.sendGet(url);
            log.info("result is : {}", result);
            ThirdUser thirdUser = JsonCommon.snakeParse(result, ThirdUser.class);
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
