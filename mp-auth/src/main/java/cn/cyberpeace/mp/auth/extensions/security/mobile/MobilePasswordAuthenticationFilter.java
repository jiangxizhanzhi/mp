 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MobilePasswordAuthenticationFilter.java.
 // File size 3812.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.extensions.security.mobile;

import cn.cyberpeace.mp.auth.util.PasswordUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

 /**
  * 手机密码认证过滤器
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Slf4j
public class MobilePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

     /**
      *
      */
     public static final String SPRING_SECURITY_FORM_MOBILE_KEY = "mobile";

     /**
      * 弹簧安全表单密码密钥
      */
     public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";

     /**
      * 默认蚂蚁路径请求匹配器
      */
     private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/login/mobile",
            "POST");

     /**
      * 手机参数
      */
     private String mobileParameter = SPRING_SECURITY_FORM_MOBILE_KEY;

     /**
      * 密码参数
      */
     private String passwordParameter = SPRING_SECURITY_FORM_PASSWORD_KEY;

     /**
      * 后只
      */
     private boolean postOnly = true;

     /**
      * 手机密码认证过滤器
      */
     public MobilePasswordAuthenticationFilter() {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

     /**
      * 手机密码认证过滤器
      *
      * @param authenticationManager 身份验证管理器
      */
     public MobilePasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
    }

     /**
      * 获取移动参数
      *
      * @return {@link String }
      */
     public final String getMobileParameter() {
        return this.mobileParameter;
    }

     /**
      * 获取密码参数
      *
      * @return {@link String }
      */
     public final String getPasswordParameter() {
        return this.passwordParameter;
    }

     /**
      * 尝试验证
      *
      * @param request  请求
      * @param response 响应
      * @return {@link Authentication }
      * @throws AuthenticationException 身份验证异常
      * @throws IOException             ioexception
      * @throws ServletException        servlet异常
      */
     @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        if (!Objects.equals(request.getMethod(), "POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        if (Objects.equals(request.getContentType(), MediaType.APPLICATION_JSON_VALUE)) {
            Map<String, String> loginData = new HashMap<>();
            try {
                loginData = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            } catch (IOException e) {
                log.error("Parse login data from request error: {}", e.getMessage());
            }
            String mobile = loginData.getOrDefault(getMobileParameter(), StringUtils.EMPTY);
            String decryptedPwd = loginData.getOrDefault(getPasswordParameter(), StringUtils.EMPTY);
            String originPwd = PasswordUtils.desDecrypt(decryptedPwd);
            var authRequest = new MobilePasswordAuthenticationToken(mobile, originPwd);
            setDetails(request, authRequest);
            // 认证
            return getAuthenticationManager().authenticate(authRequest);
        } else {
            throw new AuthenticationServiceException("ContentType not supported: " + request.getContentType());
        }
    }

     /**
      * 设置细节
      *
      * @param request     请求
      * @param authRequest
      */
     protected void setDetails(HttpServletRequest request, MobilePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }
}
