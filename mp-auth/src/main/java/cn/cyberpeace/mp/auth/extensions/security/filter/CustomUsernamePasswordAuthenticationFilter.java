package cn.cyberpeace.mp.auth.extensions.security.filter;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.util.KaptchaUtil;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Slf4j
@Data
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private Cache<String, Integer> loginFailureCache;

    @SneakyThrows
    @Override
    @SuppressWarnings("unchecked")
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
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
            String username = loginData.getOrDefault(getUsernameParameter(), StringUtils.EMPTY);
            Integer failureCount = loginFailureCache.getIfPresent(username);
            request.setAttribute(AuthConstant.REQUEST_PARAMETER_USERNAME, username);
            if (loginData.containsKey(AuthConstant.REQUEST_PARAMETER_VERIFY_CODE) || (Objects.nonNull(failureCount) && failureCount >= 3)) {
                ServiceResult<Object> result = KaptchaUtil.checkKaptcha(loginData.getOrDefault(AuthConstant.REQUEST_PARAMETER_VERIFY_CODE, StringUtils.EMPTY));
                if (Boolean.FALSE.equals(result.getSource())) {
                    KaptchaUtil.checkFail(response, result);
                    return null;
                }
            }
            String decryptedPwd = loginData.getOrDefault(getPasswordParameter(), StringUtils.EMPTY);
            String originPwd = PasswordUtils.desDecrypt(decryptedPwd);
            username = username.trim();
            var authRequest = new UsernamePasswordAuthenticationToken(username, originPwd);
            setDetails(request, authRequest);
            // 认证
            return getAuthenticationManager().authenticate(authRequest);
        } else {
            String verifyCode = request.getParameter(AuthConstant.REQUEST_PARAMETER_VERIFY_CODE);
            String username = request.getParameter("username");
            Integer failureCount = loginFailureCache.getIfPresent(username);
            if (StringUtils.isNotBlank(verifyCode) || (Objects.nonNull(failureCount) && failureCount >= 3)) {
                ServiceResult<Object> result = KaptchaUtil.checkKaptcha(verifyCode);
                if (Boolean.FALSE.equals(result.getSource())) {
                    KaptchaUtil.checkFail(response, result);
                    return null;
                }
            }
            return super.attemptAuthentication(request, response);
        }
    }
}
