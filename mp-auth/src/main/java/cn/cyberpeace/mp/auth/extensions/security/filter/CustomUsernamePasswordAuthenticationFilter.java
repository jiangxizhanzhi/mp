package cn.cyberpeace.mp.auth.extensions.security.filter;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.util.KaptchaUtil;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Slf4j
@Data
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private Cache<String, Integer> loginFailureCache;

    @SneakyThrows
    @Override
    @SuppressWarnings("unchecked")
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        if (!Objects.equals(request.getMethod(), "POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        if (Objects.equals(request.getContentType(), MediaType.APPLICATION_JSON_VALUE)) {
            Map<String, String> loginData = new HashMap<>();
            try {
                loginData = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            } catch (IOException e) {
                log.error("Parse login data from request error: {}", e.getMessage());
                return null;
            }
            var authRequest = processAuthRequest(loginData, request, response);
            
            return getAuthenticationManager().authenticate(authRequest);
        } else {
            return super.attemptAuthentication(request, response);
        }
    }

    private boolean checkVerifyCode(String verifyCode, String username, HttpServletResponse response) throws IOException {
        String needVerifyCode = AuthConstant.LOGIN_NEED_VERIFY_CODE + ":" + username;
        Integer flag = loginFailureCache.getIfPresent(needVerifyCode);
        if (Objects.nonNull(flag)) { 
            ServiceResult<Object> result = KaptchaUtil.checkKaptcha(verifyCode);
            if (Boolean.FALSE.equals(result.getSource())) { 
                KaptchaUtil.checkFail(response, result);
                return false;
            }
            
            loginFailureCache.invalidate(needVerifyCode);
        }
        return true;
    }


    private UsernamePasswordAuthenticationToken processAuthRequest(Map<String, String> loginData, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = loginData.getOrDefault(getUsernameParameter(), StringUtils.EMPTY);
        String decryptedPwd = loginData.getOrDefault(getPasswordParameter(), StringUtils.EMPTY);

        if (StringUtils.isNotBlank(username)) {
            request.setAttribute(AuthConstant.REQUEST_PARAMETER_USERNAME, username);
            String verifyCode = loginData.getOrDefault(AuthConstant.REQUEST_PARAMETER_VERIFY_CODE, StringUtils.EMPTY);
            boolean valid = checkVerifyCode(verifyCode, username, response);
            if (!valid) {
                return null;
            }
        }

        ServiceResult<Object> result = null;
        if (StringUtils.isBlank(username)) {
            result = ServiceResult.builder().serviceCode(AuthErrorCode.USERNAME_EMPTY).build().asResult();
        }

        if (Objects.isNull(result)) {
            if (StringUtils.isBlank(decryptedPwd)) {
                result = ServiceResult.builder().serviceCode(AuthErrorCode.PASSWORD_EMPTY).build().asResult();
            }
        }

        String originPwd = "";
        if (Objects.isNull(result)) {
            try {
                originPwd = PasswordUtils.desDecrypt(decryptedPwd);
            } catch (Exception e) {
                result = ServiceResult.builder().serviceCode(AuthErrorCode.PASSWORD_DECODE_ERROR).build().asResult();
            }
        }

        if (Objects.nonNull(result)) {
            PrintWriter writer = response.getWriter();
            MappingJackson2HttpMessageConverter converter = BeanCommon.getBean(MappingJackson2HttpMessageConverter.class);
            converter.getObjectMapper().writeValue(writer, result);
            writer.flush();
            return null;
        }

        username = username.trim();
        var authRequest = new UsernamePasswordAuthenticationToken(username, originPwd);
        setDetails(request, authRequest);
        return authRequest;
    }

}
