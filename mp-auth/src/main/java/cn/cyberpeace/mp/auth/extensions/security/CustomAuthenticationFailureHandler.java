 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CustomAuthenticationFailureHandler.java.
 // File size 5383.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.extensions.security;

import cn.cyberpeace.mp.auth.config.AuthCacheConfig;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

 /**
  * 自定义身份验证失败处理程序
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
     /**
      * 映射器
      */
     private final ObjectMapper mapper;

     /**
      * 自定义身份验证失败处理程序
      *
      * @param messageConverter 消息转换器
      */
     @Autowired
    public CustomAuthenticationFailureHandler(MappingJackson2HttpMessageConverter messageConverter) {
        this.mapper = messageConverter.getObjectMapper();
    }

     /**
      * 登录失败缓存
      */
     @Resource(name = AuthCacheConfig.LOGIN_FAILURE)
    private Cache<String, Integer> loginFailureCache;

     /**
      * 鉴权失败
      *
      * @param request   请求
      * @param response  响应
      * @param exception 异常
      * @throws IOException ioexception
      */
     @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        ServiceResult<Object> result = null;
        if (exception instanceof BadCredentialsException) {
            response.setStatus(HttpServletResponse.SC_OK);
            String username = getUserName(request);
            String pwdErrorCountCacheKey = AuthConstant.PASSWORD_ERROR_COUNT_KEY + ":" + username;
            String needVerifyCode = AuthConstant.LOGIN_NEED_VERIFY_CODE + ":" + username;
            Integer count = loginFailureCache.getIfPresent(pwdErrorCountCacheKey);
            if (Objects.nonNull(count) && count >= 2) {
                loginFailureCache.put(needVerifyCode, 1);
                result = ServiceResult.builder().serviceCode(AuthErrorCode.NEED_VERIFY_CODE).build().asResult();
            } else {
                result = ServiceResult.builder().serviceCode(AuthErrorCode.BAD_CREDENTIALS).build().asResult();
            }
            loginFailureCache.put(pwdErrorCountCacheKey, Objects.isNull(count) ? 1 : count + 1);
        }

        if (exception instanceof UsernameNotFoundException) {
            response.setStatus(HttpServletResponse.SC_OK);
            result = ServiceResult.builder().serviceCode(AuthErrorCode.BAD_CREDENTIALS).build().asResult();
        }

        // 弃用，因为无法控制异常状态顺序：状态优先级 禁用 > 过期 > 待审核
        if (exception instanceof AccountStatusException) {
            response.setStatus(HttpServletResponse.SC_OK);

            if (exception instanceof DisabledException) {
                result = ServiceResult.builder().serviceCode(AuthErrorCode.ACCOUNT_DISABLED).build().asResult();
            }

            if (exception instanceof AccountExpiredException) {
                result = ServiceResult.builder().serviceCode(AuthErrorCode.ACCOUNT_CREDENTIALS_EXPIRED).build().asResult();
            }

            if (exception instanceof CredentialsExpiredException) {
                result = ServiceResult.builder().serviceCode(AuthErrorCode.ACCOUNT_EXPIRED).build().asResult();
            }

            if (exception instanceof LockedException) {
                result = ServiceResult.builder().serviceCode(AuthErrorCode.ACCOUNT_NOT_AUDITED).build().asResult();
            }
        }

        if (Objects.isNull(result)) {
            logger.info("authentication failed!");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result = ServiceResult.builder().serviceCode(AuthErrorCode.AUTHENTICATION_FAILED).build().asResult();
        }

        PrintWriter writer = response.getWriter();
        mapper.writeValue(writer, result);
        writer.flush();
    }

     /**
      * 获取用户名
      *
      * @param request 请求
      * @return {@link String }
      */
     private String getUserName(HttpServletRequest request) {
        if (Objects.equals(request.getContentType(), MediaType.APPLICATION_JSON_VALUE)) {
            return (String) request.getAttribute(AuthConstant.REQUEST_PARAMETER_USERNAME);
        } else {
            return request.getParameter(AuthConstant.REQUEST_PARAMETER_USERNAME);
        }
    }
}
