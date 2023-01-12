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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
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
            request.setAttribute(AuthConstant.REQUEST_PARAMETER_USERNAME, username);
            String verifyCode = loginData.getOrDefault(AuthConstant.REQUEST_PARAMETER_VERIFY_CODE, StringUtils.EMPTY);
            boolean valid = checkVerifyCode(verifyCode, username, request, response);
            if (!valid) {
                return null;
            }
            String decryptedPwd = loginData.getOrDefault(getPasswordParameter(), StringUtils.EMPTY);
            ServiceResult<Object> result = null;
            if (StringUtils.isBlank(decryptedPwd)) {
                result = ServiceResult.builder().serviceCode(AuthErrorCode.PASSWORD_EMPTY).build().asResult();
            }

            String originPwd = "";
            if (Objects.isNull(result)) {
                try {
                    originPwd = PasswordUtils.desDecrypt(decryptedPwd);
                } catch (Exception e) {
                    result = ServiceResult.builder().serviceCode(AuthErrorCode.BAD_CREDENTIALS).build().asResult();
                }
            }

            if (Objects.nonNull(result)) {
                PrintWriter writer = response.getWriter();
                response.setContentType("application/json;charset=UTF-8");
                MappingJackson2HttpMessageConverter converter = BeanCommon.getBean(MappingJackson2HttpMessageConverter.class);
                converter.getObjectMapper().writeValue(writer, result);
                writer.flush();
                return null;
            }

            username = username.trim();
            var authRequest = new UsernamePasswordAuthenticationToken(username, originPwd);
            setDetails(request, authRequest);
            // 认证
            return getAuthenticationManager().authenticate(authRequest);
        } else {
            String verifyCode = request.getParameter(AuthConstant.REQUEST_PARAMETER_VERIFY_CODE);
            String username = request.getParameter(AuthConstant.REQUEST_PARAMETER_USERNAME);
            boolean valid = checkVerifyCode(verifyCode, username, request, response);
            if (!valid) {
                return null;
            }
            return super.attemptAuthentication(request, response);
        }
    }

    private boolean checkVerifyCode(String verifyCode, String username, HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Boolean flag = (Boolean) session.getAttribute(AuthConstant.NEED_VERIFY_CODE_FLAG_PREFIX + username);
        if (Objects.nonNull(flag) && Boolean.TRUE.equals(flag)) { // 需要校验验证码
            ServiceResult<Object> result = KaptchaUtil.checkKaptcha(verifyCode);
            if (Boolean.FALSE.equals(result.getSource())) { // 验证码校验不通过
                session.setAttribute(AuthConstant.NEED_VERIFY_CODE_FLAG_PREFIX + username, true);
                session.setMaxInactiveInterval(AuthConstant.NEED_VERIFY_CODE_EXPIRE_TIME);
                KaptchaUtil.checkFail(response, result);
                return false;
            }
            // 验证码校验通过，移除需要验证码标记
            session.removeAttribute(AuthConstant.NEED_VERIFY_CODE_FLAG_PREFIX + username);
        }
        return true;
    }
}
