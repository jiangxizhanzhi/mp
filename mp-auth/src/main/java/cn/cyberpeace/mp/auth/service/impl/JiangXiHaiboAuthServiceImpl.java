package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.extensions.security.filter.JwtAuthenticationFilter;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.ThirdAuthService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.JwtUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Objects;

@Slf4j
@Service
public class JiangXiHaiboAuthServiceImpl implements ThirdAuthService {

    private final String VERIFY_USER_BY_TOKEN;
    private final String LOGOUT_USER_BY_TOKEN;

    private final AuthConfig authConfig;


    private final RestTemplate camelRestTemplate;

    private final UserService userService;

    @Autowired
    public JiangXiHaiboAuthServiceImpl(AuthConfig authConfig, @Qualifier("camelRestTemplate") RestTemplate camelRestTemplate, UserService userService) {
        this.authConfig = authConfig;
        this.camelRestTemplate = camelRestTemplate;
        this.userService = userService;

        this.VERIFY_USER_BY_TOKEN = String.format("%s?token={token}", authConfig.getHaiboTokenUrl());
        this.LOGOUT_USER_BY_TOKEN = String.format("%s?token={token}", authConfig.getHaiboLogoutUrl());
    }

    @Override
    public void loginByToken(String token, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String encodeToken = URLEncoder.encode(token, Charset.forName("utf-8"));
        log.info("origin token:{},encode token:{}", encodeToken);
        RequestEntity<Void> request = RequestEntity.get(VERIFY_USER_BY_TOKEN, encodeToken)
                .accept(MediaType.APPLICATION_JSON).build();
        HaiboResponse<HaiboUser> response = camelRestTemplate.exchange(request, new ParameterizedTypeReference<HaiboResponse<HaiboUser>>() {
        }).getBody();

        if (Objects.nonNull(response) && response.responseSuccessFlag()) {
            HaiboUser haiboUser = response.data;
            UserModel userModel = userService.getUserModelById(haiboUser.getUserId());
            if (Objects.isNull(userModel)) {
                log.error("根据haibo userId获取用户信息失败, haiboUser = {}", haiboUser);
                throw new MpRuntimeException(AuthErrorCode.ACCOUNT_NOT_EXISTED);
            } else {
                String newJwt = JwtUtils.generateToken(new JwtUtils.UserInfo(userModel.getId(), userModel.getUsername(), userModel.getTokenSecret()));
                JwtAuthenticationFilter.addJWTCookieToResponse(httpServletResponse, newJwt);
                userService.updateUserHaiboToken(userModel, token);

            }
        } else {
            String errorMessage = "用户信息请求验证失败";
            if (Objects.nonNull(response) && StringUtils.isNotBlank(response.getMessage())) {
                errorMessage = response.getMessage();
            }
            throw new MpRuntimeException(errorMessage);
        }
    }

    @Override
    public void logout(String userId) {
        String haiboToken = userService.getUserModelById(userId).getHaiboToken();
        if (StringUtils.isNotBlank(haiboToken)) {
            RequestEntity<Void> request = RequestEntity.get(LOGOUT_USER_BY_TOKEN, haiboToken)
                    .accept(MediaType.APPLICATION_JSON).build();
            HaiboResponse<Void> response = camelRestTemplate.exchange(request, new ParameterizedTypeReference<HaiboResponse<Void>>() {
            }).getBody();
            if (Objects.nonNull(response) && response.responseSuccessFlag()) {
                return;
            } else {
                throw new MpRuntimeException(AuthErrorCode.LOGOUT_FAIL);
            }
        }
    }

    @Data
    public static class HaiboResponse<T> {

        @JsonProperty("Total")
        private Integer total;

        @JsonProperty("Data")
        private T data;

        @JsonProperty("Tag")
        private Integer tag;

        @JsonProperty("Message")
        private String message;

        @JsonProperty("Description")
        private String description;

        boolean responseSuccessFlag() {
            return Objects.nonNull(tag) && tag == 1;
        }
    }

    @Data
    public static class HaiboUser {
        private String userId;
        private String name;
        private String token;
        private String username;
    }
}
