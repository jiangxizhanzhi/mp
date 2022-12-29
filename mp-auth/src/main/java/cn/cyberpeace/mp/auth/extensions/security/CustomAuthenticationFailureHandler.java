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
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
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
        ServiceResult result = null;
        if(exception instanceof BadCredentialsException) {
            response.setStatus(HttpServletResponse.SC_OK);
            String username = getUserName(request);
            Integer count = loginFailureCache.getIfPresent(username);
            if (Objects.nonNull(count) && count >= 2) {
                HttpSession session = request.getSession();
                session.setAttribute(AuthConstant.NEED_VERIFY_CODE_FLAG_PREFIX + username, true);
                session.setMaxInactiveInterval(AuthConstant.NEED_VERIFY_CODE_EXPIRE_TIME);
                result = ServiceResult.builder().serviceCode(AuthErrorCode.NEED_VERIFY_CODE).build().asResult();
            } else {
                result = ServiceResult.builder().serviceCode(AuthErrorCode.BAD_CREDENTIALS).build().asResult();
            }
            loginFailureCache.put(username, Objects.isNull(count) ? 1 : count + 1);
        }

        if(exception instanceof UsernameNotFoundException) {
            response.setStatus(HttpServletResponse.SC_OK);
            result = ServiceResult.builder().serviceCode(AuthErrorCode.BAD_CREDENTIALS).build().asResult();
        }

        // 弃用，因为无法控制异常状态顺序：状态优先级 禁用 > 过期 > 待审核
        if(exception instanceof AccountStatusException) {
            response.setStatus(HttpServletResponse.SC_OK);

            if(exception instanceof DisabledException) {
                result = ServiceResult.builder().serviceCode(AuthErrorCode.ACCOUNT_DISABLED).build().asResult();
            }

            if(exception instanceof AccountExpiredException) {
                result = ServiceResult.builder().serviceCode(AuthErrorCode.ACCOUNT_CREDENTIALS_EXPIRED).build().asResult();
            }

            if(exception instanceof CredentialsExpiredException) {
                result = ServiceResult.builder().serviceCode(AuthErrorCode.ACCOUNT_EXPIRED).build().asResult();
            }

            if(exception instanceof LockedException) {
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
