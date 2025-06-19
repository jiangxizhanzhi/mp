 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ThirdSsoService.java.
 // File size 8455.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.extensions.security.filter.JwtAuthenticationFilter;
import cn.cyberpeace.mp.auth.model.TokenModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.TokenMapper;
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
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static cn.cyberpeace.mp.auth.constant.AuthConstant.ROLE_ADMIN_ID;
import static cn.cyberpeace.mp.auth.constant.AuthConstant.ROOT_ORG_ID;

@Slf4j
@Service
public class ThirdSsoService {


    @Resource
    private AuthConfig authConfig;

    @Resource
    private UserService userService;

    @Resource
    private TokenMapper tokenMapper;

    public String loginByToken(String code, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String url) {
        String clientId = authConfig.getThirdClientId();
        String clientSecret = authConfig.getThirdClientSecret();
        String getTokenUrl = authConfig.getThirdTokenUrl();
        String getUserInfoUrl = authConfig.getThirdUserInfoUrl();
        String getCodeUrl = String.format("%s/oauth?client_id=%s&redirect_uri=%s/api/mp/unauth/third-login?%s", authConfig.getThirdIp(), clientId, authConfig.getMpIp(), url);

        if (code == null) {
            return String.format("redirect:%s", getCodeUrl);
        } else {
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
                UserModel userModel = userService.getByUserName(userInfo.getUserAccount());
                if (Objects.isNull(userModel)) {
                    // 新增一个用户
                    UserRequest userRequest = new UserRequest();
                    userRequest.setUsername(userInfo.getUserAccount());
                    userRequest.setName(userInfo.getUserName());
                    userRequest.setRoleIds(Collections.singletonList(ROLE_ADMIN_ID));
                    userRequest.setOrgIds(Collections.singletonList(ROOT_ORG_ID));
                    userRequest.setPassword("d8165e80c4abed2dcc1dd06df1a8be31");
                    userModel = userService.add(userRequest, true);
                }
                // 更新下菜单
                UserModel updateModel = new UserModel();
                updateModel.setId(userModel.getId());
                updateModel.setMenusContent(JsonCommon.stringify(userInfo.getRoleMenu()));
                userService.update(updateModel);

                String newJwt = JwtUtils.generateToken(new JwtUtils.UserInfo(userModel.getId(), userModel.getUsername(), userModel.getTokenSecret()));
                JwtAuthenticationFilter.addJWTCookieToResponse(httpServletResponse, newJwt);
                JwtAuthenticationFilter.addThirdTokenToResponse(httpServletResponse, thirdToken.getLoginToken());

                TokenModel tokenModel = new TokenModel();
                tokenModel.setLoginToken(thirdToken.getLoginToken());
                tokenModel.setAccessToken(thirdToken.getAccessToken());
                tokenModel.setJwt(newJwt);
                tokenModel.setUserName(userModel.getUsername());
                tokenModel.setCreateTime(LocalDateTime.now());
                log.info("tokeModel:[{}]", tokenModel);
                tokenMapper.create(tokenModel);
            } catch (Exception e) {
                log.error("faild to loginByToken ", e);
                throw new RuntimeException(e);
            }
            return String.format("redirect:%s", url);
        }
    }

    public Boolean tokenCheck(String token) {
        String clientId = authConfig.getThirdClientId();
        String tokenCheckUrl = authConfig.getThirdCheckUrl();
        TokenCheckParams tokenCheckParams = new TokenCheckParams();
        tokenCheckParams.setClientId(clientId);
        tokenCheckParams.setLoginToken(token);
        String params = JsonCommon.snakeStringify(tokenCheckParams);

        log.info("check token , url is {} , params is {}", tokenCheckUrl, params);
        String result = null;
        try {
            result = HttpUtils.sendPost(tokenCheckUrl, params);
            log.info("check token , result is {}", result);
        } catch (Exception e) {
            log.error("faild to tokenCheck ", e);
            throw new RuntimeException(e);
        }
        ThirdResponse<TokenCheckResult> thirdResponse = JsonCommon.snakeParse(result, new TypeReference<ThirdResponse<TokenCheckResult>>() {
        });
        if (thirdResponse.getCode() != 0) {
            List<TokenModel> tokenModels = tokenMapper.getByLoginToken(token);
            if (CollectionUtils.isNotEmpty(tokenModels)) {
                for (TokenModel tokenModel : tokenModels){
                    tokenMapper.delete(tokenModel.getId());
                }
            }
            return false;
        } else {
            return true;
        }
    }

    public void updateMenus(String accessToken) throws Exception {

        // 查询用户信息
        GetUserinfoParams getUserinfoParams = new GetUserinfoParams();
        getUserinfoParams.setAccessToken(accessToken);
        getUserinfoParams.setClientId(authConfig.getThirdClientId());
        String params = JsonCommon.snakeStringify(getUserinfoParams);
        log.info("query user info , url is {} , params is {}", authConfig.getThirdUserInfoUrl(), params);
        String result = HttpUtils.sendPost(authConfig.getThirdUserInfoUrl(), params);
        log.info("query user info , result is {}", result);
        ThirdResponse<ThirdUserInfo> userResponse = JsonCommon.snakeParse(result, new TypeReference<ThirdResponse<ThirdUserInfo>>() {
        });
        var userInfo = userResponse.getData();
        UserModel userModel = userService.getByUserName(userInfo.getUserAccount());

        // 更新下菜单
        UserModel updateModel = new UserModel();
        updateModel.setId(userModel.getId());
        updateModel.setMenusContent(JsonCommon.stringify(userInfo.getRoleMenu()));
        userService.update(updateModel);

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
        private List<ThirdRole> roleMenu;
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

    @Getter
    @Setter
    public static class ThirdRole {
        private Integer roleId;
        private String roleName;
        private List<ThirdRoleMenu> menu;
    }

    @Getter
    @Setter
    public static class ThirdRoleMenu {
        private String name;
        private String label;
        private String path;
        private String icon;
        private String component;
        private List<ThirdRoleMenu> child;
    }

}
