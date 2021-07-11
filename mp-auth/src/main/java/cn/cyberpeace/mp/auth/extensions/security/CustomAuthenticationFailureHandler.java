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


@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final ObjectMapper mapper;

    @Autowired
    public CustomAuthenticationFailureHandler(MappingJackson2HttpMessageConverter messageConverter) {
        this.mapper = messageConverter.getObjectMapper();
    }

    @Resource(name = AuthCacheConfig.LOGIN_FAILURE)
    private Cache<String, Integer> loginFailureCache;

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

    private String getUserName(HttpServletRequest request) {
        if (Objects.equals(request.getContentType(), MediaType.APPLICATION_JSON_VALUE)) {
            return (String) request.getAttribute(AuthConstant.REQUEST_PARAMETER_USERNAME);
        } else {
            return request.getParameter(AuthConstant.REQUEST_PARAMETER_USERNAME);
        }
    }
}
