package cn.cyberpeace.mp.auth.extensions.security;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        ServiceResult result = null;
        if(exception instanceof BadCredentialsException) {
            response.setStatus(HttpServletResponse.SC_OK);
            result = ServiceResult.builder().serviceCode(AuthErrorCode.BAD_CREDENTIALS).build().asResult();
        }

        // 弃用，因为无法控制异常状态顺序：状态优先级 禁用 > 过期 > 待审核
        /*if(exception instanceof AccountStatusException) {
            response.setStatus(HttpServletResponse.SC_OK);

            if(exception instanceof DisabledException) {
                result = ServiceResult.builder().serviceCode(AuthErrorCode.ACCOUNT_DISABLED).build().asResult();
            }

            if(exception instanceof AccountExpiredException) {
                result = ServiceResult.builder().serviceCode(AuthErrorCode.ACCOUNT_EXPIRED).build().asResult();
            }

            if(exception instanceof LockedException) {
                result = ServiceResult.builder().serviceCode(AuthErrorCode.ACCOUNT_NOT_AUDITED).build().asResult();
            }
        }*/

        if (Objects.isNull(result)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result = ServiceResult.builder().serviceCode(AuthErrorCode.AUTHENTICATION_FAILED).build().asResult();
        }

        PrintWriter writer = response.getWriter();
        mapper.writeValue(writer, result);
        writer.flush();
    }
}
